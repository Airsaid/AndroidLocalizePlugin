package com.airsaid.localization.translate.impl.google

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.components.service

@Service
@State(name = "com.airsaid.localization.GoogleTranslatorSettings", storages = [Storage("googleTranslatorSettings.xml")])
class GoogleTranslatorSettings : PersistentStateComponent<GoogleTranslatorSettings.State> {

  data class State(
    var useCustomServer: Boolean = false,
    var serverUrl: String = DEFAULT_SERVER_URL
  )

  private var state = State()

  var useCustomServer: Boolean
    get() = state.useCustomServer
    set(value) {
      state = state.copy(useCustomServer = value)
    }

  var serverUrl: String
    get() = state.serverUrl.ifBlank { DEFAULT_SERVER_URL }
    set(value) {
      state = state.copy(serverUrl = value.ifBlank { DEFAULT_SERVER_URL })
    }

  override fun getState(): State = state

  override fun loadState(state: State) {
    this.state = state
  }

  companion object {
    const val DEFAULT_SERVER_URL = "https://translate.googleapis.com"

    fun getInstance(): GoogleTranslatorSettings = service()
  }
}
