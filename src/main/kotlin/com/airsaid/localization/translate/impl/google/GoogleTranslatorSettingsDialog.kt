package com.airsaid.localization.translate.impl.google

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
    get() = 400 to 160

  private val settings = GoogleTranslatorSettings.getInstance()

  init {
    title = "Google Translator Settings"
  }

  @Composable
  override fun Content() {
    var useCustomServer by remember { mutableStateOf(settings.useCustomServer) }
    var serverUrl by remember { mutableStateOf(settings.serverUrl) }

    Column(
      modifier = Modifier
        .padding(horizontal = 20.dp, vertical = 16.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      IdeCheckBox(
        checked = useCustomServer,
        onCheckedChange = {
          useCustomServer = it
          if (!it) {
            serverUrl = settings.serverUrl
          }
        },
        title = "Use custom server",
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

      SelectionContainer {
        Text(
          text = "Defaults to translate.googleapis.com when not specified.",
          color = JewelTheme.globalColors.text.info
        )
      }
    }

    OnClickOK {
      // Persist the selected endpoint and toggle when the user accepts the dialog.
      settings.useCustomServer = useCustomServer
      if (useCustomServer) {
        settings.serverUrl = serverUrl.ifBlank { GoogleTranslatorSettings.DEFAULT_SERVER_URL }
      }
    }
  }
}