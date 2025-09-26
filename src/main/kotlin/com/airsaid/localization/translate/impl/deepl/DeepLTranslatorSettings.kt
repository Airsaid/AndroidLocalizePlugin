package com.airsaid.localization.translate.impl.deepl

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.components.service

/**
 * Persistent storage for DeepL translator runtime preferences.
 *
 * @author airsaid
 */
@Service
@State(name = "com.airsaid.localization.DeepLTranslatorSettings", storages = [Storage("deeplTranslatorSettings.xml")])
class DeepLTranslatorSettings : PersistentStateComponent<DeepLTranslatorSettings.State> {

  data class State(
    var usePro: Boolean = false
  )

  private var state = State()

  var usePro: Boolean
    get() = state.usePro
    set(value) {
      state = state.copy(usePro = value)
    }

  override fun getState(): State = state

  override fun loadState(state: State) {
    this.state = state
  }

  companion object {
    fun getInstance(): DeepLTranslatorSettings = service()
  }
}
