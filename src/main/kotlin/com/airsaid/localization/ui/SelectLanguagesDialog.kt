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

package com.airsaid.localization.ui

import com.airsaid.localization.config.SettingsState
import com.airsaid.localization.constant.Constants
import com.airsaid.localization.translate.lang.Lang
import com.airsaid.localization.translate.services.TranslatorService
import com.airsaid.localization.utils.LanguageUtil
import com.intellij.ide.util.PropertiesComponent
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBScrollPane
import com.intellij.util.ui.JBUI
import java.awt.Component
import java.awt.GridLayout
import java.awt.event.ItemEvent
import javax.swing.JCheckBox
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.BoxLayout

/**
 * Select the language dialog you want to Translate.
 *
 * @author airsaid
 */
class SelectLanguagesDialog(private val project: Project?) : DialogWrapper(project, false) {

    interface OnClickListener {
        fun onClickListener(selectedLanguage: List<Lang>)
    }

    private val contentPanel = JPanel()
    private val overwriteExistingStringCheckBox = JBCheckBox("Overwrite existing strings")
    private val selectAllCheckBox = JBCheckBox("Select all")
    private val languagesPanel = JPanel()
    private val openTranslatedFileCheckBox = JBCheckBox("Open translated file after completion")
    private val powerTranslatorLabel = JBLabel()

    private var onClickListener: OnClickListener? = null
    private val selectedLanguages = mutableListOf<Lang>()

    init {
        setupUi()
        doCreateCenterPanel()
        title = "Select Translated Languages"
        init()
    }

    fun setOnClickListener(listener: OnClickListener) {
        onClickListener = listener
    }

    override fun createCenterPanel(): JComponent? {
        return contentPanel
    }

    private fun setupUi() {
        contentPanel.layout = java.awt.BorderLayout(0, 12)
        contentPanel.border = JBUI.Borders.empty(12)

        languagesPanel.layout = GridLayout(0, 4, 12, 4)

        val scrollPane = JBScrollPane(languagesPanel)
        scrollPane.border = JBUI.Borders.empty()
        contentPanel.add(scrollPane, java.awt.BorderLayout.CENTER)

        val optionsPanel = JPanel()
        optionsPanel.layout = BoxLayout(optionsPanel, BoxLayout.Y_AXIS)
        optionsPanel.border = JBUI.Borders.emptyTop(8)

        listOf<JComponent>(selectAllCheckBox, overwriteExistingStringCheckBox, openTranslatedFileCheckBox, powerTranslatorLabel).forEach {
            it.alignmentX = Component.LEFT_ALIGNMENT
            optionsPanel.add(it)
        }

        contentPanel.add(optionsPanel, java.awt.BorderLayout.SOUTH)
    }

    private fun doCreateCenterPanel() {
        // add languages
        selectedLanguages.clear()
        val supportedLanguages = TranslatorService.getInstance().getSelectedTranslator()!!.supportedLanguages
        val sortedLanguages = supportedLanguages.toMutableList()
        sortedLanguages.sortWith(EnglishNameComparator()) // sort by english name, easy to find
        languagesPanel.removeAll()
        addLanguageList(sortedLanguages)

        // add options
        initOverwriteExistingStringOption()
        initOpenTranslatedFileCheckBox()
        initSelectAllOption()

        // set power ui
        val translator = TranslatorService.getInstance().getSelectedTranslator()!!
        powerTranslatorLabel.text = "Powered by ${translator.name}"
        powerTranslatorLabel.icon = translator.icon
    }

    private fun addLanguageList(supportedLanguages: List<Lang>) {
        val selectedLanguageIds = LanguageUtil.getSelectedLanguageIds(project)
        for (language in supportedLanguages) {
            val code = language.code
            val checkBoxLanguage = JBCheckBox()
            checkBoxLanguage.text = "${language.englishName}($code)"
            languagesPanel.add(checkBoxLanguage)
            checkBoxLanguage.addItemListener { e ->
                val state = e.stateChange
                if (state == ItemEvent.SELECTED) {
                    if (!selectedLanguages.contains(language)) {
                        selectedLanguages.add(language)
                    }
                } else {
                    selectedLanguages.remove(language)
                }
                // Update the OK button UI
                okAction.isEnabled = selectedLanguages.size > 0
            }
            if (selectedLanguageIds?.contains(language.id.toString()) == true) {
                checkBoxLanguage.isSelected = true
            }
        }
        languagesPanel.revalidate()
        languagesPanel.repaint()
        okAction.isEnabled = selectedLanguages.isNotEmpty()
    }

    private fun initOverwriteExistingStringOption() {
        val isOverwriteExistingString = PropertiesComponent.getInstance(project!!)
            .getBoolean(Constants.KEY_IS_OVERWRITE_EXISTING_STRING)
        overwriteExistingStringCheckBox.isSelected = isOverwriteExistingString
        overwriteExistingStringCheckBox.addItemListener { e ->
            val state = e.stateChange
            PropertiesComponent.getInstance(project!!)
                .setValue(Constants.KEY_IS_OVERWRITE_EXISTING_STRING, state == ItemEvent.SELECTED)
        }
    }

    private fun initOpenTranslatedFileCheckBox() {
        val isOpenTranslatedFile = PropertiesComponent.getInstance(project!!)
            .getBoolean(Constants.KEY_IS_OPEN_TRANSLATED_FILE)
        openTranslatedFileCheckBox.isSelected = isOpenTranslatedFile
        openTranslatedFileCheckBox.addItemListener { e ->
            val state = e.stateChange
            PropertiesComponent.getInstance(project!!)
                .setValue(Constants.KEY_IS_OPEN_TRANSLATED_FILE, state == ItemEvent.SELECTED)
        }
    }

    private fun initSelectAllOption() {
        val isSelectAll = PropertiesComponent.getInstance(project!!)
            .getBoolean(Constants.KEY_IS_SELECT_ALL)
        selectAllCheckBox.isSelected = isSelectAll
        selectAllCheckBox.addItemListener { e ->
            val state = e.stateChange
            selectAll(state == ItemEvent.SELECTED)
            PropertiesComponent.getInstance(project!!)
                .setValue(Constants.KEY_IS_SELECT_ALL, state == ItemEvent.SELECTED)
        }
    }

    private fun selectAll(selectAll: Boolean) {
        for (component in languagesPanel.components) {
            if (component is JBCheckBox) {
                component.isSelected = selectAll
            }
        }
    }

    override fun getDimensionServiceKey(): String? {
        val key = SettingsState.getInstance().selectedTranslator.key
        return "#com.airsaid.localization.ui.SelectLanguagesDialog#$key"
    }

    override fun doOKAction() {
        LanguageUtil.saveSelectedLanguage(project!!, selectedLanguages)
        onClickListener?.onClickListener(selectedLanguages)
        super.doOKAction()
    }

    class EnglishNameComparator : Comparator<Lang> {
        override fun compare(o1: Lang, o2: Lang): Int {
            return o1.englishName.compareTo(o2.englishName)
        }
    }
}
