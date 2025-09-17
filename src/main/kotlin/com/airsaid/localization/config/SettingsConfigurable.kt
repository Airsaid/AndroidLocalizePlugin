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

import com.airsaid.localization.constant.Constants
import com.airsaid.localization.translate.AbstractTranslator
import com.airsaid.localization.translate.services.TranslatorService
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.options.Configurable
import com.intellij.openapi.options.ConfigurationException
import com.intellij.openapi.util.text.StringUtil
import javax.swing.JComponent

/**
 * @author airsaid
 */
class SettingsConfigurable : Configurable {
    companion object {
        private val LOG = Logger.getInstance(SettingsConfigurable::class.java)
    }

    private var settingsComponent: SettingsComponent? = null

    override fun getDisplayName(): String {
        return Constants.PLUGIN_NAME
    }

    override fun getPreferredFocusedComponent(): JComponent? {
        return settingsComponent?.preferredFocusedComponent
    }

    override fun createComponent(): JComponent? {
        settingsComponent = SettingsComponent()
        initComponents()
        return settingsComponent?.content
    }

    private fun initComponents() {
        val settingsState = SettingsState.getInstance()
        val translators = TranslatorService.getInstance().getTranslators()
        settingsComponent?.let { component ->
            component.setTranslators(translators)
            component.setSelectedTranslator(translators[settingsState.selectedTranslator.key]!!)
            component.setEnableCache(settingsState.isEnableCache)
            component.setMaxCacheSize(settingsState.maxCacheSize)
            component.setTranslationInterval(settingsState.translationInterval)
        }
    }

    override fun isModified(): Boolean {
        val settingsState = SettingsState.getInstance()
        val selectedTranslator = settingsComponent?.selectedTranslator ?: return false

        var isChanged = settingsState.selectedTranslator != selectedTranslator
        isChanged = isChanged || settingsState.getAppId(selectedTranslator.key) != selectedTranslator.appId
        isChanged = isChanged || settingsState.getAppKey(selectedTranslator.key) != selectedTranslator.appKey
        isChanged = isChanged || settingsState.isEnableCache != (settingsComponent?.isEnableCache ?: false)
        isChanged = isChanged || settingsState.maxCacheSize != (settingsComponent?.maxCacheSize ?: 0)
        isChanged = isChanged || settingsState.translationInterval != (settingsComponent?.translationInterval ?: 0)

        LOG.info("isModified: $isChanged")
        return isChanged
    }

    @Throws(ConfigurationException::class)
    override fun apply() {
        val settingsState = SettingsState.getInstance()
        val selectedTranslator = settingsComponent?.selectedTranslator
            ?: throw ConfigurationException("No translator selected")

        LOG.info("apply selectedTranslator: ${selectedTranslator.name}")

        // Verify that the required parameters are not configured
        if (selectedTranslator.isNeedAppId && StringUtil.isEmpty(settingsComponent?.appId)) {
            throw ConfigurationException("${selectedTranslator.appIdDisplay} not configured")
        }
        if (selectedTranslator.isNeedAppKey && StringUtil.isEmpty(settingsComponent?.appKey)) {
            throw ConfigurationException("${selectedTranslator.appKeyDisplay} not configured")
        }

        settingsState.selectedTranslator = selectedTranslator
        if (selectedTranslator.isNeedAppId) {
            settingsComponent?.appId?.let { appId ->
                settingsState.setAppId(selectedTranslator.key, appId)
            }
        }
        if (selectedTranslator.isNeedAppKey) {
            settingsComponent?.appKey?.let { appKey ->
                settingsState.setAppKey(selectedTranslator.key, appKey)
            }
        }

        settingsComponent?.let { component ->
            settingsState.isEnableCache = component.isEnableCache
            settingsState.maxCacheSize = component.maxCacheSize
            settingsState.translationInterval = component.translationInterval

            val translatorService = TranslatorService.getInstance()
            translatorService.setSelectedTranslator(selectedTranslator)
            translatorService.setEnableCache(component.isEnableCache)
            translatorService.maxCacheSize = component.maxCacheSize
            translatorService.translationInterval = component.translationInterval
        }
    }

    override fun reset() {
        LOG.info("reset")
        val settingsState = SettingsState.getInstance()
        val selectedTranslator = settingsState.selectedTranslator
        settingsComponent?.let { component ->
            component.setSelectedTranslator(selectedTranslator)
            component.setAppId(settingsState.getAppId(selectedTranslator.key))
            component.setAppKey(settingsState.getAppKey(selectedTranslator.key))
            component.setEnableCache(settingsState.isEnableCache)
            component.setMaxCacheSize(settingsState.maxCacheSize)
            component.setTranslationInterval(settingsState.translationInterval)
        }
    }

    override fun disposeUIResources() {
        settingsComponent = null
    }
}