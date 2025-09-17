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
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.xml.XmlTag

/**
 * Translate android string value to other languages that can be used to localize your Android APP.
 *
 * @author airsaid
 */
class TranslateAction : AnAction(), SelectLanguagesDialog.OnClickListener {

    private lateinit var project: Project
    private lateinit var valueFile: PsiFile
    private lateinit var values: List<PsiElement>
    private val valueService = AndroidValuesService.getInstance()

    override fun actionPerformed(e: AnActionEvent) {
        project = e.getRequiredData(CommonDataKeys.PROJECT)
        valueFile = e.getRequiredData(CommonDataKeys.PSI_FILE)

        SettingsState.getInstance().initSetting()

        valueService.loadValuesByAsync(valueFile) { loadedValues ->
            if (!isTranslatable(loadedValues)) {
                NotificationUtil.notifyInfo(project, "The ${valueFile.name} has no text to translate.")
                return@loadValuesByAsync
            }
            values = loadedValues
            showSelectLanguageDialog()
        }
    }

    // Verify that there is a text in the value file that needs to be translated.
    private fun isTranslatable(values: List<PsiElement>): Boolean {
        for (psiElement in values) {
            if (psiElement is XmlTag) {
                if (valueService.isTranslatable(psiElement)) {
                    return true
                }
            }
        }
        return false
    }

    private fun showSelectLanguageDialog() {
        val dialog = SelectLanguagesDialog(project)
        dialog.setOnClickListener(this)
        dialog.show()
    }

    override fun update(e: AnActionEvent) {
        // The translation option is only show when xml file from values is selected
        val project = e.getData(CommonDataKeys.PROJECT)
        val isSelectValueFile = valueService.isValueFile(e.getData(CommonDataKeys.PSI_FILE))
        e.presentation.setEnabledAndVisible(project != null && isSelectValueFile)
    }

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
}