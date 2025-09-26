package com.airsaid.localization.translate.impl.google

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.airsaid.localization.ui.ComposeDialog
import com.airsaid.localization.ui.components.IdeCheckBox
import com.airsaid.localization.ui.components.IdeTextField
import java.awt.Dimension

/**
 * Compose dialog allowing users to toggle the Google translator backend address.
 *
 * @author airsaid
 */
class GoogleTranslatorSettingsDialog : ComposeDialog() {

  private val settings = GoogleTranslatorSettings.getInstance()

  init {
    title = "Google Translator Settings"
  }

  override fun preferredSize() = Dimension(400, 160)

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
        onValueChange = {
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
          style = MaterialTheme.typography.labelMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant
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
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        )
      }

      SelectionContainer {
        Text(
          text = "Defaults to translate.googleapis.com when not specified.",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
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
