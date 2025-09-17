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
import com.airsaid.localization.utils.SecureStorage
import com.intellij.openapi.components.*
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.util.text.StringUtil

/**
 * @author airsaid
 */
@State(
    name = "com.airsaid.localization.config.SettingsState",
    storages = [Storage("androidLocalizeSettings.xml")]
)
@Service
class SettingsState : PersistentStateComponent<SettingsState.State> {
    companion object {
        private val LOG = Logger.getInstance(SettingsState::class.java)

        fun getInstance(): SettingsState {
            return ServiceManager.getService(SettingsState::class.java)
        }
    }

    private val appKeyStorage: Map<String, SecureStorage>
    private var state = State()

    init {
        val storage = mutableMapOf<String, SecureStorage>()
        val translatorService = TranslatorService.getInstance()
        val translators = translatorService.getTranslators().values
        for (translator in translators) {
            if (translatorService.getDefaultTranslator() != translator) {
                storage[translator.key] = SecureStorage(translator.key)
            }
        }
        appKeyStorage = storage
    }

    fun initSetting() {
        val translatorService = TranslatorService.getInstance()
        val selectedTranslator = translatorService.getSelectedTranslator()
        if (selectedTranslator == null) {
            LOG.info("initSetting")
            translatorService.setSelectedTranslator(this.selectedTranslator)
            translatorService.setEnableCache(isEnableCache)
            translatorService.maxCacheSize = maxCacheSize
            translatorService.translationInterval = translationInterval
        }
    }

    var selectedTranslator: AbstractTranslator
        get() = if (StringUtil.isEmpty(state.selectedTranslatorKey)) {
            TranslatorService.getInstance().getDefaultTranslator()
        } else {
            TranslatorService.getInstance().getTranslators()[state.selectedTranslatorKey]
                ?: TranslatorService.getInstance().getDefaultTranslator()
        }
        set(translator) {
            state.selectedTranslatorKey = translator.key
        }

    fun setAppId(translatorKey: String, appId: String) {
        state.appIds[translatorKey] = appId
    }

    fun getAppId(translatorKey: String): String {
        return state.appIds[translatorKey] ?: ""
    }

    fun setAppKey(translatorKey: String, appKey: String) {
        val secureStorage = appKeyStorage[translatorKey]
        secureStorage?.save(appKey)
    }

    fun getAppKey(translatorKey: String): String {
        val secureStorage = appKeyStorage[translatorKey]
        return secureStorage?.read() ?: ""
    }

    var isEnableCache: Boolean
        get() = state.isEnableCache
        set(isEnable) {
            state.isEnableCache = isEnable
        }

    var maxCacheSize: Int
        get() = state.maxCacheSize
        set(maxCacheSize) {
            state.maxCacheSize = maxCacheSize
        }

    var translationInterval: Int
        get() = state.translationInterval
        set(intervalTime) {
            state.translationInterval = intervalTime
        }

    override fun getState(): State {
        return state
    }

    override fun loadState(state: State) {
        this.state = state
    }

    data class State(
        var selectedTranslatorKey: String? = null,
        var appIds: MutableMap<String, String> = mutableMapOf(),
        var isEnableCache: Boolean = true,
        var maxCacheSize: Int = 500,
        var translationInterval: Int = 2 // 2 second
    )
}