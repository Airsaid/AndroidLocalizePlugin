package com.airsaid.localization.translate.impl.openai

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.airsaid.localization.config.SettingsState
import com.airsaid.localization.config.TranslatorCredentialsDialog
import com.airsaid.localization.translate.AbstractTranslator
import com.airsaid.localization.translate.util.GsonUtil
import com.airsaid.localization.translate.util.HttpRequestFactory
import com.airsaid.localization.ui.components.IdeCheckBox
import com.airsaid.localization.ui.components.IdeDropdownField
import com.airsaid.localization.ui.components.IdeTextField
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.ui.component.Text

/**
 * Compose dialog for managing OpenAI translator credentials, hosts, and models.
 *
 * @author airsaid
 */
class OpenAITranslatorSettingsDialog(
  translator: AbstractTranslator,
  settingsState: SettingsState,
) : TranslatorCredentialsDialog(translator, settingsState) {

  override val defaultPreferredSize
    get() = 480 to 400

  private val openAISettings = OpenAITranslatorSettings.getInstance()

  init {
    title = "OpenAI Settings"
  }

  @Composable
  override fun Content() {
    var selectedModel by remember { mutableStateOf(openAISettings.selectedModel) }
    var useCustomModel by remember { mutableStateOf(openAISettings.useCustomModel) }
    var customModel by remember { mutableStateOf(openAISettings.customModel) }
    var apiHost by remember { mutableStateOf(openAISettings.apiHost) }
    var availableModels by remember { mutableStateOf(openAISettings.cachedModels) }
    var loadingModels by remember { mutableStateOf(false) }
    var loadErrorMessage by remember { mutableStateOf<String?>(null) }
    var lastSyncedAt by remember { mutableStateOf(openAISettings.lastModelSyncTimestamp) }
    var lastFetchedBase by remember { mutableStateOf(openAISettings.resolvedBaseUrl()) }

    fun buildBaseUrl(): String = OpenAITranslatorSettings.normalizeBaseUrl(apiHost)

    fun ensureSelectedModel(models: List<String>) {
      if (!useCustomModel) {
        if (models.isEmpty()) {
          selectedModel = ""
        } else if (selectedModel.isBlank() || selectedModel !in models) {
          selectedModel = models.first()
        }
      }
    }

    val apiKeyState = credentialValuesState[API_KEY_ID].orEmpty()

    LaunchedEffect(Unit) {
      ensureSelectedModel(availableModels)
    }

    LaunchedEffect(apiKeyState, apiHost, lastSyncedAt) {
      val trimmedKey = apiKeyState.trim()
      if (trimmedKey.isBlank()) {
        availableModels = openAISettings.cachedModels
        ensureSelectedModel(availableModels)
        return@LaunchedEffect
      }

      if (loadingModels) return@LaunchedEffect

      val normalizedBase = buildBaseUrl()
      val shouldRefresh =
        availableModels.isEmpty() || openAISettings.shouldRefreshModels() || normalizedBase != lastFetchedBase
      if (!shouldRefresh) {
        availableModels = openAISettings.cachedModels
        ensureSelectedModel(availableModels)
        return@LaunchedEffect
      }

      loadingModels = true
      loadErrorMessage = null
      val result = withContext(Dispatchers.IO) {
        runCatching { fetchModels(normalizedBase, trimmedKey) }
      }
      loadingModels = false
      result.onSuccess { models ->
        openAISettings.updateCachedModels(models)
        availableModels = openAISettings.cachedModels
        ensureSelectedModel(availableModels)
        if (!useCustomModel) {
          selectedModel = openAISettings.selectedModel
        }
        lastSyncedAt = openAISettings.lastModelSyncTimestamp
        lastFetchedBase = normalizedBase
      }.onFailure { error ->
        loadErrorMessage = "Failed to load models: ${error.message}"
      }
    }

    Column(
      modifier = Modifier
        .padding(horizontal = 20.dp, vertical = 16.dp),
      verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
      Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
          text = "API Key",
          color = JewelTheme.globalColors.text.info
        )
        IdeTextField(
          value = credentialValuesState[API_KEY_ID].orEmpty(),
          onValueChange = { newValue -> credentialValuesState[API_KEY_ID] = newValue.trimStart() },
          modifier = Modifier.fillMaxWidth(),
          singleLine = true,
          secureInput = true,
          placeholder = {
            Text(
              text = "sk-...",
              color = JewelTheme.globalColors.text.info
            )
          }
        )
      }

      Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
          text = "API Endpoint",
          color = JewelTheme.globalColors.text.info
        )
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          IdeTextField(
            value = apiHost,
            onValueChange = { apiHost = it.trimStart() },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            placeholder = {
              Text(
                text = OpenAITranslatorSettings.DEFAULT_API_HOST,
                color = JewelTheme.globalColors.text.info
              )
            }
          )
          Text(
            text = "Leave blank to use the default OpenAI endpoint.",
            color = JewelTheme.globalColors.text.info
          )
        }
      }

      Text(
        text = "Model",
        color = JewelTheme.globalColors.text.info
      )
      Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        IdeCheckBox(
          checked = useCustomModel,
          onCheckedChange = {
            useCustomModel = it
            if (!it) {
              ensureSelectedModel(availableModels)
              selectedModel = openAISettings.selectedModel
            }
          },
          title = "Use custom model"
        )

        if (useCustomModel) {
          IdeTextField(
            value = customModel,
            onValueChange = { customModel = it.trimStart() },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            placeholder = {
              Text(
                text = "e.g. gpt-4.1-custom",
                color = JewelTheme.globalColors.text.info
              )
            }
          )
        } else {
          IdeDropdownField(
            value = selectedModel,
            enabled = availableModels.isNotEmpty(),
            options = availableModels,
            placeholder = if (availableModels.isEmpty()) "No models available" else "Select model",
            loading = loadingModels,
            onOptionSelected = { model -> selectedModel = model }
          )

          loadErrorMessage?.let { error ->
            Text(
              text = error,
              color = Color(0xFFE53935)
            )
          }

          if (!loadingModels && loadErrorMessage == null) {
            val helperText = if (lastSyncedAt > 0L) {
              val formattedDate = java.time.Instant.ofEpochMilli(lastSyncedAt)
                .atZone(java.time.ZoneId.systemDefault())
                .toLocalDate()
              "Model list refreshes daily when an API key is configured. Last synced: $formattedDate"
            } else {
              "Model list refreshes daily when an API key is configured."
            }
            Text(
              text = helperText,
              color = JewelTheme.globalColors.text.info
            )
          }
        }
      }
    }

    // Persist the dialog selections so subsequent requests reuse them.
    OnClickOK {
      openAISettings.selectedModel = selectedModel
      openAISettings.useCustomModel = useCustomModel
      openAISettings.customModel = customModel.trim()
      openAISettings.apiHost = apiHost.trim()
    }
  }

  @Throws(Exception::class)
  private fun fetchModels(baseUrl: String, apiKey: String): List<String> {
    val request = HttpRequestFactory.get(buildUrl(baseUrl, "/v1/models"))
    request.tuner { connection ->
      connection.setRequestProperty("Authorization", "Bearer $apiKey")
      connection.setRequestProperty("Content-Type", "application/json")
    }
    val json = request.connect { it.readString() }
    val response = GsonUtil.getInstance().gson.fromJson(json, OpenAIModelsResponse::class.java)
    return response.data.mapNotNull { it.id.takeIf { id -> id.isNotBlank() } }.sorted()
  }

  private fun buildUrl(base: String, path: String): String {
    val prefix = base.trimEnd('/')
    val suffix = path.trimStart('/')
    return "$prefix/$suffix"
  }

  companion object {
    private const val API_KEY_ID = "appKey"
  }
}