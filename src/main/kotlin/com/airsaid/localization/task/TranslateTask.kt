/*
 * Copyright 2021 Airsaid. https://github.com/airsaid
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.airsaid.localization.task

import com.airsaid.localization.constant.Constants
import com.airsaid.localization.services.AndroidValuesService
import com.airsaid.localization.translate.TranslationException
import com.airsaid.localization.translate.lang.Lang
import com.airsaid.localization.translate.lang.Languages
import com.airsaid.localization.translate.lang.toLang
import com.airsaid.localization.translate.services.TranslatorService
import com.airsaid.localization.utils.TextUtil
import com.intellij.ide.util.PropertiesComponent
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Computable
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.xml.XmlTag
import com.intellij.psi.xml.XmlText
import java.io.File
import java.util.*
import java.util.stream.Collectors

/**
 * @author airsaid
 */
class TranslateTask(
  project: Project?,
  title: String,
  private val toLanguages: List<Lang>,
  private val values: List<PsiElement>,
  valueFile: PsiFile
) : Task.Backgroundable(project, title) {

  companion object {
    private const val NAME_TAG_STRING = "string"
    private const val NAME_TAG_PLURALS = "plurals"
    private const val NAME_TAG_STRING_ARRAY = "string-array"
    private val LOG = Logger.getInstance(TranslateTask::class.java)
  }

  interface OnTranslateListener {
    fun onTranslateSuccess()
    fun onTranslateError(e: Throwable)
  }

  private val valueFile: VirtualFile = valueFile.virtualFile
  private val translatorService = TranslatorService.getInstance()
  private val valueService = AndroidValuesService.getInstance()
  private var onTranslateListener: OnTranslateListener? = null

  @Volatile
  private var translationError: TranslationException? = null

  /**
   * Set translate result listener.
   *
   * @param listener callback interface. success or fail.
   */
  fun setOnTranslateListener(listener: OnTranslateListener) {
    onTranslateListener = listener
  }

  override fun run(progressIndicator: ProgressIndicator) {
    val isOverwriteExistingString = PropertiesComponent.getInstance(project)
      .getBoolean(Constants.KEY_IS_OVERWRITE_EXISTING_STRING)
    LOG.info("run isOverwriteExistingString: $isOverwriteExistingString")

    val totalTranslatableCount = countTranslatableValues()
    for (toLanguage in toLanguages) {
      if (progressIndicator.isCanceled) break

      updateProgressText(progressIndicator, toLanguage, 0, totalTranslatableCount)

      val resourceDir = valueFile.parent.parent
      val valueFileName = valueFile.name
      val toValuePsiFile = valueService.getValuePsiFile(project, resourceDir, toLanguage, valueFileName)
      LOG.info("Translating language: ${toLanguage.englishName}, toValuePsiFile: $toValuePsiFile")

      if (toValuePsiFile != null) {
        val toValues = valueService.loadValues(toValuePsiFile)
        val toValuesMap = toValues.stream().collect(
          Collectors.toMap(
            { psiElement ->
              if (psiElement is XmlTag) {
                ApplicationManager.getApplication().runReadAction(Computable {
                  psiElement.getAttributeValue("name") ?: UUID.randomUUID().toString()
                })
              } else {
                UUID.randomUUID().toString()
              }
            },
            { it }
          ))
        val translatedValues = doTranslate(
          progressIndicator,
          toLanguage,
          toValuesMap,
          isOverwriteExistingString,
          totalTranslatableCount
        )
        translationError?.let { return }
        writeTranslatedValues(progressIndicator, File(toValuePsiFile.virtualFile.path), translatedValues)
      } else {
        val translatedValues = doTranslate(
          progressIndicator,
          toLanguage,
          null,
          isOverwriteExistingString,
          totalTranslatableCount
        )
        translationError?.let { return }
        val valueFile = valueService.getValueFile(resourceDir, toLanguage, valueFileName)
        writeTranslatedValues(progressIndicator, valueFile, translatedValues)
      }

      // If an exception occurs during the translation of the language,
      // terminate the task to avoid reporting an IDE error and leave the listener to handle the failure.
      translationError?.let { return }
    }
  }

  private fun doTranslate(
    progressIndicator: ProgressIndicator,
    toLanguage: Lang,
    toValues: Map<String, PsiElement>?,
    isOverwrite: Boolean,
    totalTranslatableCount: Int
  ): List<PsiElement> {
    LOG.info("doTranslate toLanguage: ${toLanguage.englishName}, toValues: $toValues, isOverwrite: $isOverwrite")

    val translatedValues = ArrayList<PsiElement>()
    var translatedCount = 0
    for (value in values) {
      if (translationError != null) break
      if (progressIndicator.isCanceled) break

      if (value is XmlTag) {
        if (!valueService.isTranslatable(value)) {
          continue
        }

        translatedCount += 1
        updateProgressText(progressIndicator, toLanguage, translatedCount, totalTranslatableCount)

        val name = ApplicationManager.getApplication().runReadAction(Computable {
          value.getAttributeValue("name")
        })

        if (!isOverwrite && toValues != null && toValues.containsKey(name)) {
          toValues[name]?.let { translatedValues.add(it) }
          continue
        }

        val translateValue = ApplicationManager.getApplication().runReadAction(Computable {
          value.copy() as XmlTag
        })

        translatedValues.add(translateValue)
        when (translateValue.name) {
          NAME_TAG_STRING -> {
            doTranslate(progressIndicator, toLanguage, translateValue)
          }

          NAME_TAG_STRING_ARRAY, NAME_TAG_PLURALS -> {
            val subTags = ApplicationManager.getApplication()
              .runReadAction(Computable { translateValue.subTags })
            for (subTag in subTags) {
              if (translationError != null) break
              doTranslate(progressIndicator, toLanguage, subTag)
            }
          }
        }
      } else {
        translatedValues.add(value)
      }
    }
    return translatedValues
  }

  private fun doTranslate(
    progressIndicator: ProgressIndicator,
    toLanguage: Lang,
    xmlTag: XmlTag
  ) {
    if (progressIndicator.isCanceled || isXliffTag(xmlTag) || translationError != null) return

    val xmlTagValue = ApplicationManager.getApplication()
      .runReadAction(Computable { xmlTag.value })
    val children = xmlTagValue.children

    for (child in children) {
      if (translationError != null) break
      when (child) {
        is XmlText -> {
          val text = ApplicationManager.getApplication()
            .runReadAction(Computable { child.value })
          if (TextUtil.isEmptyOrSpacesLineBreak(text)) {
            continue
          }
          try {
            val translatedText = translatorService.doTranslate(Languages.AUTO.toLang(), toLanguage, text)
            ApplicationManager.getApplication().runReadAction {
              child.value = translatedText
            }
          } catch (e: TranslationException) {
            LOG.warn(e)
            // Just catch the error and wait for that file to be translated and released.
            translationError = e
            return
          }
        }

        is XmlTag -> {
          doTranslate(progressIndicator, toLanguage, child)
        }
      }
    }
  }

  private fun writeTranslatedValues(
    progressIndicator: ProgressIndicator,
    valueFile: File,
    translatedValues: List<PsiElement>
  ) {
    LOG.info("writeTranslatedValues valueFile: $valueFile, translatedValues: $translatedValues")

    if (progressIndicator.isCanceled || translatedValues.isEmpty()) return

    progressIndicator.text = "Writing to ${valueFile.parentFile.name} data..."
    valueService.writeValueFile(translatedValues, valueFile)

    refreshAndOpenFile(valueFile)
  }

  private fun countTranslatableValues(): Int {
    return values.count { value ->
      value is XmlTag && valueService.isTranslatable(value)
    }
  }

  private fun updateProgressText(
    progressIndicator: ProgressIndicator,
    toLanguage: Lang,
    current: Int,
    total: Int
  ) {
    progressIndicator.text = if (total > 0) {
      val clampedCurrent = current.coerceAtMost(total)
      progressIndicator.fraction = clampedCurrent.toDouble() / total
      "Translating to ${toLanguage.englishName} ($clampedCurrent/$total)..."
    } else {
      progressIndicator.fraction = 0.0
      "Translating to ${toLanguage.englishName}..."
    }
  }

  private fun refreshAndOpenFile(file: File) {
    val virtualFile = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(file)
    val isOpenTranslatedFile = PropertiesComponent.getInstance(project)
      .getBoolean(Constants.KEY_IS_OPEN_TRANSLATED_FILE)
    if (virtualFile != null && isOpenTranslatedFile) {
      ApplicationManager.getApplication().invokeLater {
        FileEditorManager.getInstance(project).openFile(virtualFile, true)
      }
    }
  }

  private fun isXliffTag(xmlTag: XmlTag?): Boolean {
    return xmlTag != null && "xliff:g" == xmlTag.name
  }

  override fun onSuccess() {
    super.onSuccess()
    val error = translationError
    if (error != null) {
      translateError(error)
    } else {
      translateSuccess()
    }
  }

  override fun onThrowable(error: Throwable) {
    super.onThrowable(error)
    translateError(error)
  }

  private fun translateSuccess() {
    onTranslateListener?.onTranslateSuccess()
  }

  private fun translateError(error: Throwable) {
    onTranslateListener?.onTranslateError(error)
  }
}
