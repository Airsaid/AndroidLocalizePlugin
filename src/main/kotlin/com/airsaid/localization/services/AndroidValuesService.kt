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

package com.airsaid.localization.services

import com.airsaid.localization.translate.lang.Lang
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Computable
import com.intellij.openapi.util.io.FileUtil
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.psi.xml.XmlFile
import com.intellij.psi.xml.XmlTag
import com.intellij.util.Consumer
import java.io.*
import java.nio.charset.StandardCharsets
import java.util.regex.Pattern

/**
 * Operation service for the android value files. eg: strings.xml (or any string resource from values directory).
 *
 * @author airsaid
 */
@Service
class AndroidValuesService {

  var isSkipNonTranslatable: Boolean = false

  companion object {
    private val LOG = Logger.getInstance(AndroidValuesService::class.java)
    private val STRINGS_FILE_NAME_PATTERN = Pattern.compile(".+\\.xml")

    /**
     * Returns the [AndroidValuesService] object instance.
     *
     * @return the [AndroidValuesService] object instance.
     */
    fun getInstance(): AndroidValuesService {
      return ServiceManager.getService(AndroidValuesService::class.java)
    }
  }

  /**
   * Asynchronous loading the value file as the [PsiElement] collection.
   *
   * @param valueFile the value file.
   * @param consumer  load result. called in the event dispatch thread.
   */
  fun loadValuesByAsync(valueFile: PsiFile, consumer: Consumer<List<PsiElement>>) {
    ApplicationManager.getApplication().executeOnPooledThread {
      val values = loadValues(valueFile)
      ApplicationManager.getApplication().invokeLater {
        consumer.consume(values)
      }
    }
  }

  /**
   * Loading the value file as the [PsiElement] collection.
   *
   * @param valueFile the value file.
   * @return [PsiElement] collection.
   */
  fun loadValues(valueFile: PsiFile): List<PsiElement> {
    return ApplicationManager.getApplication().runReadAction(Computable {
      LOG.info("loadValues valueFile: ${valueFile.name}")
      val values = parseValuesXml(valueFile)
      LOG.info("loadValues parsed ${valueFile.name} result: $values")
      values
    })
  }

  private fun parseValuesXml(valueFile: PsiFile): List<PsiElement> {
    val xmlFile = valueFile as XmlFile

    val document = xmlFile.document ?: return emptyList()
    val rootTag = document.rootTag ?: return emptyList()

    val subTags = rootTag.children

    if (!isSkipNonTranslatable) {
      return subTags.toList()
    }

    val values = mutableListOf<PsiElement>()
    var skipNext = false

    for (element in subTags) {
      if (skipNext) {
        skipNext = false
        if (element !is XmlTag) {
          continue
        }
      }
      if (element is XmlTag && !isTranslatable(element)) {
        skipNext = true
      } else {
        values.add(element)
      }
    }

    return values
  }

  /**
   * Write [PsiElement] collection data to the specified file.
   *
   * @param values    specified [PsiElement] collection data.
   * @param valueFile specified file.
   */
  fun writeValueFile(values: List<PsiElement>, valueFile: File) {
    val isCreateSuccess = FileUtil.createIfDoesntExist(valueFile)
    if (!isCreateSuccess) {
      LOG.error("Failed to write to ${valueFile.path} file: create failed!")
      return
    }

    ApplicationManager.getApplication().invokeLater {
      ApplicationManager.getApplication().runWriteAction {
        try {
          BufferedWriter(OutputStreamWriter(FileOutputStream(valueFile, false), StandardCharsets.UTF_8)).use { bw ->
            for (value in values) {
              bw.write(value.text)
            }
            bw.flush()
          }
        } catch (e: IOException) {
          e.printStackTrace()
          LOG.error("Failed to write to ${valueFile.path} file.", e)
        }
      }
    }
  }

  /**
   * Verify that the specified file is a string resource file in the values directory.
   *
   * @param file the verify file.
   * @return true: the file is a string resource file in the values directory.
   */
  fun isValueFile(file: PsiFile?): Boolean {
    if (file == null) return false

    val parent = file.parent ?: return false
    val parentName = parent.name
    if ("values" != parentName) return false

    val fileName = file.name
    return STRINGS_FILE_NAME_PATTERN.matcher(fileName).matches()
  }

  /**
   * Get the value file of the specified language in the specified project resource directory.
   *
   * @param project     current project.
   * @param resourceDir specified resource directory.
   * @param lang        specified language.
   * @param fileName    the name of value file.
   * @return null if not exist, otherwise return the value file.
   */
  fun getValuePsiFile(
    project: Project,
    resourceDir: VirtualFile,
    lang: Lang,
    fileName: String
  ): PsiFile? {
    return ApplicationManager.getApplication().runReadAction(Computable {
      val virtualFile = LocalFileSystem.getInstance().findFileByIoFile(getValueFile(resourceDir, lang, fileName))
        ?: return@Computable null
      PsiManager.getInstance(project).findFile(virtualFile)
    })
  }

  /**
   * Get the value file in the `values` directory of the specified language in the resource directory.
   *
   * @param resourceDir specified resource directory.
   * @param lang        specified language.
   * @param fileName    the name of value file.
   * @return the value file.
   */
  fun getValueFile(resourceDir: VirtualFile, lang: Lang, fileName: String): File {
    return File(resourceDir.path + File.separator + getValuesDirectoryName(lang), fileName)
  }

  private fun getValuesDirectoryName(lang: Lang): String {
    val directoryName = lang.directoryName
    return if (directoryName.isBlank()) "values" else "values-$directoryName"
  }

  /**
   * Returns whether the specified xml tag (string entry) needs to be translated.
   *
   * @param xmlTag the specified xml tag of string entry.
   * @return true: need translation. false: no translation is needed.
   */
  fun isTranslatable(xmlTag: XmlTag): Boolean {
    return ApplicationManager.getApplication().runReadAction(Computable {
      val translatableStr = xmlTag.getAttributeValue("translatable")
      (translatableStr ?: "true").toBoolean()
    })
  }
}
