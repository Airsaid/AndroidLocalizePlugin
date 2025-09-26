package com.airsaid.localization.translate.impl.openai

import com.intellij.openapi.components.*
import java.net.URI

/**
 * Persisted configuration controlling OpenAI translator models and API host.
 *
 * @author airsaid
 */
@Service
@State(
  name = "com.airsaid.localization.OpenAITranslatorSettings",
  storages = [Storage("openAITranslatorSettings.xml")]
)
class OpenAITranslatorSettings : PersistentStateComponent<OpenAITranslatorSettings.State> {

  data class State(
    var selectedModel: String = DEFAULT_MODEL,
    var useCustomModel: Boolean = false,
    var customModel: String = "",
    var apiHost: String = "",
    var cachedModels: MutableList<String> = mutableListOf(),
    var lastModelSyncTimestamp: Long = 0L,
  )

  private var state = State()

  var selectedModel: String
    get() = state.selectedModel.ifBlank { DEFAULT_MODEL }
    set(value) {
      state = state.copy(selectedModel = value.ifBlank { DEFAULT_MODEL })
    }

  var useCustomModel: Boolean
    get() = state.useCustomModel
    set(value) {
      state = state.copy(useCustomModel = value)
    }

  var customModel: String
    get() = state.customModel
    set(value) {
      state = state.copy(customModel = value)
    }

  var apiHost: String
    get() = state.apiHost
    set(value) {
      state = state.copy(apiHost = value.trim())
    }

  var cachedModels: List<String>
    get() = state.cachedModels.takeIf { it.isNotEmpty() } ?: DEFAULT_MODELS
    private set(value) {
      state = state.copy(cachedModels = value.toMutableList())
    }

  var lastModelSyncTimestamp: Long
    get() = state.lastModelSyncTimestamp
    private set(value) {
      state = state.copy(lastModelSyncTimestamp = value)
    }

  fun resolvedModel(): String {
    val custom = customModel.trim()
    return if (useCustomModel && custom.isNotEmpty()) {
      custom
    } else {
      selectedModel
    }
  }

  fun resolvedBaseUrl(): String = normalizeBaseUrl(apiHost)

  fun shouldRefreshModels(currentTimeMillis: Long = System.currentTimeMillis()): Boolean {
    return currentTimeMillis - lastModelSyncTimestamp >= MODEL_CACHE_TTL_MS
  }

  fun updateCachedModels(models: List<String>, fetchTimestamp: Long = System.currentTimeMillis()) {
    val sanitized = models.filter { it.isNotBlank() }.map { it.trim() }
    cachedModels = if (sanitized.isEmpty()) DEFAULT_MODELS else sanitized.distinct().sorted()
    lastModelSyncTimestamp = fetchTimestamp

    if (!useCustomModel && selectedModel !in cachedModels) {
      selectedModel = cachedModels.firstOrNull() ?: DEFAULT_MODEL
    }
  }

  override fun getState(): State = state

  override fun loadState(state: State) {
    this.state = state.copy(
      cachedModels = state.cachedModels.takeIf { it.isNotEmpty() }?.toMutableList() ?: DEFAULT_MODELS.toMutableList()
    )
  }

  companion object {
    const val DEFAULT_API_HOST = "https://api.openai.com"
    val DEFAULT_MODELS: List<String> = listOf(
      "gpt-5", "gpt-5-mini", "gpt-5-nano",
      "gpt-4.1", "gpt-4.1-mini", "gpt-4.1-nano"
    )
    val DEFAULT_MODEL: String = DEFAULT_MODELS.first()

    private val MODEL_CACHE_TTL_MS: Long = java.time.Duration.ofDays(1).toMillis()

    fun getInstance(): OpenAITranslatorSettings = service()

    /**
     * Sanitizes the configured host into a valid absolute URL used for API calls.
     */
    fun normalizeBaseUrl(host: String): String {
      val rawHost = host.trim().ifBlank { DEFAULT_API_HOST }
      val hostWithScheme = ensureScheme(rawHost)
      val sanitized = hostWithScheme.removeSuffix("/")
      return runCatching { URI(sanitized) }
        .map { uri ->
          URI(uri.scheme, uri.userInfo, uri.host, uri.port, uri.path, uri.query, uri.fragment).toString()
        }
        .getOrDefault(sanitized)
    }

    private fun ensureScheme(host: String): String {
      return if (host.startsWith("http://") || host.startsWith("https://")) {
        host
      } else {
        "https://$host"
      }
    }
  }
}
