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

import com.airsaid.localization.services.AndroidValuesService
import com.airsaid.localization.translate.AbstractTranslator
import com.airsaid.localization.translate.TranslatorCredentialDescriptor
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

  private val credentialSecureStorage = mutableMapOf<String, SecureStorage>()
  private var state = State()

  fun initSetting() {
    val translatorService = TranslatorService.getInstance()
    translatorService.setSelectedTranslator(this.selectedTranslator)
    translatorService.setEnableCache(isEnableCache)
    translatorService.maxCacheSize = maxCacheSize
    translatorService.translationInterval = translationInterval

    AndroidValuesService.getInstance().isSkipNonTranslatable = isSkipNonTranslatable
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

  fun setCredential(translatorKey: String, descriptor: TranslatorCredentialDescriptor, value: String) {
    if (descriptor.isSecret) {
      secureStorage(translatorKey, descriptor.id).save(value)
    } else {
      val credentials = state.credentials.getOrPut(translatorKey) { mutableMapOf() }
      if (value.isBlank()) {
        credentials.remove(descriptor.id)
        if (credentials.isEmpty()) {
          state.credentials.remove(translatorKey)
        }
      } else {
        credentials[descriptor.id] = value
      }
    }
  }

  fun getCredential(translatorKey: String, descriptor: TranslatorCredentialDescriptor): String {
    return if (descriptor.isSecret) {
      readSecret(translatorKey, descriptor)
    } else {
      state.credentials[translatorKey]?.get(descriptor.id) ?: ""
    }
  }

  fun getCredentials(translatorKey: String, descriptors: List<TranslatorCredentialDescriptor>): Map<String, String> {
    if (descriptors.isEmpty()) return emptyMap()
    return buildMap {
      descriptors.forEach { descriptor ->
        put(descriptor.id, getCredential(translatorKey, descriptor))
      }
    }
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

  var isSkipNonTranslatable: Boolean
    get() = state.isSkipNonTranslatable
    set(isSkipNonTranslatable) {
      state.isSkipNonTranslatable = isSkipNonTranslatable
    }

  override fun getState(): State {
    return state
  }

  override fun loadState(state: State) {
    this.state = state
    normalizeTranslationInterval()
  }

  data class State(
    var selectedTranslatorKey: String? = null,
    var credentials: MutableMap<String, MutableMap<String, String>> = mutableMapOf(),
    var isEnableCache: Boolean = true,
    var maxCacheSize: Int = 500,
    var translationInterval: Int = 500, // milliseconds
    var isSkipNonTranslatable: Boolean = false,
  )

  private fun secureStorage(translatorKey: String, credentialId: String): SecureStorage {
    val key = "$translatorKey::$credentialId"
    return credentialSecureStorage.getOrPut(key) { SecureStorage(key) }
  }

  private fun normalizeTranslationInterval() {
    if (state.translationInterval in 1..10) {
      state.translationInterval *= 1000
    }
    if (state.translationInterval <= 0) {
      state.translationInterval = 500
    }
  }

  private fun readSecret(translatorKey: String, descriptor: TranslatorCredentialDescriptor): String {
    val storage = secureStorage(translatorKey, descriptor.id)
    val value = storage.read()
    if (value.isNotEmpty()) {
      return value
    }

    // Backwards compatibility: migrate legacy key-only storage if present.
    val legacy = credentialSecureStorage.getOrPut(translatorKey) { SecureStorage(translatorKey) }
    val legacyValue = legacy.read()
    if (legacyValue.isNotEmpty()) {
      storage.save(legacyValue)
      return legacyValue
    }
    return ""
  }
}
