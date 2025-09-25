package com.airsaid.localization.translate.impl.openai

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
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
import java.awt.Dimension

class OpenAITranslatorSettingsDialog(
  translator: AbstractTranslator,
  settingsState: SettingsState,
) : TranslatorCredentialsDialog(translator, settingsState) {

  private val openAISettings = OpenAITranslatorSettings.getInstance()

  init {
    title = "OpenAI Settings"
  }

  override fun preferredSize() = Dimension(480, 400)

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
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant
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
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        )
      }

      Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
          text = "API Endpoint",
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant
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
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          )
          Text(
            text = "Leave blank to use the default OpenAI endpoint.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }

      Text(
        text = "Model",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
      Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        IdeCheckBox(
          checked = useCustomModel,
          onValueChange = {
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
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
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
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.error
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
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
      }
    }

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
