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

package com.airsaid.localization.config

import com.airsaid.localization.translate.AbstractTranslator
import com.airsaid.localization.translate.services.TranslatorService
import com.airsaid.localization.ui.FixedLinkLabel
import com.airsaid.localization.ui.SupportLanguagesDialog
import com.intellij.ide.BrowserUtil
import com.intellij.ide.HelpTooltip
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.util.text.StringUtil
import com.intellij.ui.CollectionComboBoxModel
import com.intellij.ui.SimpleListCellRenderer
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBPasswordField
import com.intellij.ui.components.JBTextField
import javax.swing.*
import java.awt.event.ItemEvent

/**
 * @author airsaid
 */
class SettingsComponent {
    companion object {
        private val LOG = Logger.getInstance(SettingsComponent::class.java)
    }

    private lateinit var contentJPanel: JPanel
    private lateinit var translatorsComboBox: ComboBox<AbstractTranslator>
    private lateinit var appIdLabel: JBLabel
    private lateinit var appIdField: JBTextField
    private lateinit var appKeyLabel: JBLabel
    private lateinit var appKeyField: JBPasswordField
    private lateinit var applyLink: FixedLinkLabel
    private lateinit var supportLanguagesButton: JButton
    private lateinit var maxCacheSizeLabel: JLabel
    private lateinit var enableCacheCheckBox: JBCheckBox
    private lateinit var maxCacheSizeComboBox: ComboBox<String>
    private lateinit var translationIntervalComboBox: ComboBox<String>

    init {
        initTranslatorComponents()
        initCacheComponents()
    }

    private fun initTranslatorComponents() {
        translatorsComboBox.renderer = object : SimpleListCellRenderer<AbstractTranslator>() {
            override fun customize(
                list: JList<out AbstractTranslator>,
                value: AbstractTranslator,
                index: Int,
                selected: Boolean,
                hasFocus: Boolean
            ) {
                text = value.name
                icon = value.icon
            }
        }

        translatorsComboBox.addItemListener { itemEvent ->
            if (itemEvent.stateChange == ItemEvent.SELECTED) {
                setSelectedTranslator(selectedTranslator)
            }
        }

        applyLink.setListener({ _, _ ->
            val selectedTranslator = selectedTranslator
            val applyAppIdUrl = selectedTranslator.applyAppIdUrl
            if (!StringUtil.isEmpty(applyAppIdUrl)) {
                BrowserUtil.browse(applyAppIdUrl!!)
                applyLink.isFocusable = false
            }
        }, null)

        supportLanguagesButton.addActionListener {
            showSupportLanguagesDialog(selectedTranslator)
        }
    }

    private fun initCacheComponents() {
        enableCacheCheckBox.addItemListener { event ->
            when (event.stateChange) {
                ItemEvent.SELECTED -> setEnableCache(true)
                ItemEvent.DESELECTED -> setEnableCache(false)
            }
        }
    }

    val selectedTranslator: AbstractTranslator
        get() = translatorsComboBox.selectedItem as AbstractTranslator

    private fun showSupportLanguagesDialog(selectedTranslator: AbstractTranslator) {
        SupportLanguagesDialog(selectedTranslator).show()
    }

    val content: JPanel
        get() = contentJPanel

    val preferredFocusedComponent: JComponent
        get() = translatorsComboBox

    fun setTranslators(translators: Map<String, AbstractTranslator>) {
        LOG.info("setTranslators: ${translators.keys}")
        translatorsComboBox.model = CollectionComboBoxModel(ArrayList(translators.values))
    }

    fun setSelectedTranslator(selected: AbstractTranslator) {
        LOG.info("setSelectedTranslator: $selected")
        translatorsComboBox.selectedItem = selected

        val isNeedAppId = selected.isNeedAppId
        appIdLabel.isVisible = isNeedAppId
        appIdField.isVisible = isNeedAppId
        if (isNeedAppId) {
            appIdLabel.text = "${selected.appIdDisplay}:"
            appIdField.text = selected.appId
        }

        val isNeedAppKey = selected.isNeedAppKey
        appKeyLabel.isVisible = isNeedAppKey
        appKeyField.isVisible = isNeedAppKey
        if (isNeedAppKey) {
            appKeyLabel.text = "${selected.appKeyDisplay}:"
            appKeyField.text = selected.appKey
        }

        val applyAppIdUrl = selected.applyAppIdUrl
        if (!StringUtil.isEmpty(applyAppIdUrl)) {
            applyLink.isVisible = true
            HelpTooltip()
                .setDescription("Apply for ${selected.name} translation API service")
                .installOn(applyLink)
        } else {
            applyLink.isVisible = false
        }
    }

    val isSelectedDefaultTranslator: Boolean
        get() = isSelectedDefaultTranslator(selectedTranslator)

    private fun isSelectedDefaultTranslator(selected: AbstractTranslator): Boolean {
        return selected == TranslatorService.getInstance().getDefaultTranslator()
    }

    val appId: String
        get() = appIdField.text ?: ""

    fun setAppId(appId: String) {
        appIdField.text = appId
    }

    val appKey: String
        get() {
            val password = appKeyField.password
            return if (password != null) String(password) else ""
        }

    fun setAppKey(appKey: String) {
        appKeyField.text = appKey
    }

    fun setEnableCache(isEnable: Boolean) {
        enableCacheCheckBox.isSelected = isEnable
        maxCacheSizeComboBox.isVisible = isEnable
        maxCacheSizeLabel.isVisible = isEnable
    }

    val isEnableCache: Boolean
        get() = enableCacheCheckBox.isSelected

    val maxCacheSize: Int
        get() = (maxCacheSizeComboBox.selectedItem as String).toInt()

    fun setMaxCacheSize(maxCacheSize: Int) {
        maxCacheSizeComboBox.selectedItem = maxCacheSize.toString()
    }

    val translationInterval: Int
        get() = (translationIntervalComboBox.selectedItem as String).toInt()

    fun setTranslationInterval(intervalTime: Int) {
        translationIntervalComboBox.selectedItem = intervalTime.toString()
    }
}