package com.airsaid.localization.translate.impl.google

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.airsaid.localization.config.SettingsState
import com.airsaid.localization.ui.ComposeDialog
import com.airsaid.localization.ui.components.IdeCheckBox
import com.airsaid.localization.ui.components.IdeTextField
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.ui.component.Text

/**
 * Compose dialog allowing users to toggle the Google translator backend address.
 *
 * @author airsaid
 */
class GoogleTranslatorSettingsDialog : ComposeDialog() {

  override val defaultPreferredSize
    get() = 500 to 260

  private val settings = GoogleTranslatorSettings.getInstance()
  private val state = SettingsState.getInstance()
  private val apiKeyDescriptor = AbsGoogleTranslator.API_KEY_DESCRIPTOR

  init {
    title = "Google Translator Settings"
  }

  @Composable
  override fun Content() {
    var useCustomServer by remember { mutableStateOf(settings.useCustomServer) }
    var serverUrl by remember { mutableStateOf(settings.serverUrl) }
    var useCustomApiKey by remember { mutableStateOf(settings.useCustomApiKey) }
    var apiKey by remember { mutableStateOf(state.getCredential("Google", apiKeyDescriptor)) }

    Column(
      modifier = Modifier
        .padding(horizontal = 20.dp, vertical = 16.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      IdeCheckBox(
        checked = useCustomServer,
        onCheckedChange = {
          useCustomServer = it
          if (useCustomServer) {
            useCustomApiKey = false
          }
        },
        title = "Use custom server",
        subTitle = "Defaults to translate.googleapis.com unless a custom server is used."
      )

      Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
          text = "Server URL",
          color = JewelTheme.globalColors.text.info
        )
        IdeTextField(
          value = serverUrl,
          onValueChange = { serverUrl = it.trimStart() },
          modifier = Modifier.fillMaxWidth(),
          singleLine = true,
          enabled = useCustomServer,
          placeholder = {
            Text(
              text = GoogleTranslatorSettings.DEFAULT_SERVER_URL,
              color = JewelTheme.globalColors.text.info
            )
          }
        )
      }

      IdeCheckBox(
        checked = useCustomApiKey,
        onCheckedChange = {
          useCustomApiKey = it
          if (useCustomApiKey) {
            useCustomServer = false
          }
        },
        title = "Use custom API key",
        subTitle = "Disable to fall back to the public web endpoint."
      )

      Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
          text = "API Key",
          color = JewelTheme.globalColors.text.info
        )
        IdeTextField(
          value = apiKey,
          onValueChange = { apiKey = it },
          modifier = Modifier.fillMaxWidth(),
          enabled = useCustomApiKey,
          secureInput = true,
          placeholder = {
            Text(
              text = "Enter your Google Cloud Translation API key",
              color = JewelTheme.globalColors.text.info
            )
          }
        )
      }
    }

    OnClickOK {
      settings.useCustomServer = useCustomServer
      if (useCustomServer) {
        settings.serverUrl = serverUrl.ifBlank { GoogleTranslatorSettings.DEFAULT_SERVER_URL }
      }
      settings.useCustomApiKey = useCustomApiKey
      val normalizedKey = if (useCustomApiKey) apiKey.trim() else ""
      state.setCredential("Google", apiKeyDescriptor, normalizedKey)
    }
  }
}
