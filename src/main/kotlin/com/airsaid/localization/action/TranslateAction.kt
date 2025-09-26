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

package com.airsaid.localization.action

import com.airsaid.localization.config.SettingsState
import com.airsaid.localization.services.AndroidValuesService
import com.airsaid.localization.task.TranslateTask
import com.airsaid.localization.translate.lang.Lang
import com.airsaid.localization.ui.SelectLanguagesDialog
import com.airsaid.localization.utils.NotificationUtil
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.psi.xml.XmlTag

/**
 * Translate android string value to other languages that can be used to localize your Android APP.
 *
 * @author airsaid
 */
class TranslateAction : AnAction() {

  override fun actionPerformed(e: AnActionEvent) {
    val project = e.getData(CommonDataKeys.PROJECT) ?: return
    val valueFile = e.getData(CommonDataKeys.PSI_FILE) ?: return
    val valueService = AndroidValuesService.getInstance()

    SettingsState.getInstance().initSetting()

    valueService.loadValuesByAsync(valueFile) { loadedValues ->
      if (!isTranslatable(loadedValues, valueService)) {
        NotificationUtil.notifyInfo(project, "The ${valueFile.name} has no text to translate.")
        return@loadValuesByAsync
      }
      showSelectLanguageDialog(project, loadedValues, valueFile)
    }
  }

  override fun update(e: AnActionEvent) {
    val project = e.project
    if (project == null) {
      e.presentation.isEnabledAndVisible = false
      return
    }

    val psiFile = resolvePsiFile(e)
    val isValueFile = AndroidValuesService.getInstance().isValueFile(psiFile)

    e.presentation.isEnabledAndVisible = isValueFile
  }

  override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.BGT

  private fun resolvePsiFile(event: AnActionEvent): PsiFile? {
    event.getData(CommonDataKeys.PSI_FILE)?.let { return it }

    val element = event.getData(LangDataKeys.PSI_ELEMENT)
    element?.containingFile?.let { return it }

    val project = event.project ?: return null
    val virtualFile = event.getData(CommonDataKeys.VIRTUAL_FILE) ?: return null
    if (virtualFile.isDirectory) {
      return null
    }

    return ReadAction.compute<PsiFile?, RuntimeException> {
      PsiManager.getInstance(project).findFile(virtualFile)
    }
  }
}

private fun isTranslatable(values: List<PsiElement>, valueService: AndroidValuesService): Boolean {
  for (psiElement in values) {
    if (psiElement is XmlTag && valueService.isTranslatable(psiElement)) {
      return true
    }
  }
  return false
}

private fun showSelectLanguageDialog(project: Project, values: List<PsiElement>, valueFile: PsiFile) {
  val dialog = SelectLanguagesDialog(project)
  dialog.setOnClickListener(object : SelectLanguagesDialog.OnClickListener {
    override fun onClickListener(selectedLanguage: List<Lang>) {
      val translationTask = TranslateTask(project, "Translating...", selectedLanguage, values, valueFile)
      translationTask.setOnTranslateListener(object : TranslateTask.OnTranslateListener {
        override fun onTranslateSuccess() {
          NotificationUtil.notifyInfo(project, "Translation completed!")
        }

        override fun onTranslateError(e: Throwable) {
          NotificationUtil.notifyError(project, "Translation failure: ${e.localizedMessage}")
        }
      })
      translationTask.queue()
    }
  })
  dialog.show()
}
