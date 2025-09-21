package com.airsaid.localization.translate.impl.google

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposePanel
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.airsaid.localization.ui.IdeTheme
import com.airsaid.localization.ui.components.IdeCheckbox
import com.airsaid.localization.ui.components.IdeTextField
import com.intellij.openapi.ui.DialogWrapper
import javax.swing.JComponent

class GoogleTranslatorSettingsDialog : DialogWrapper(true) {

  private val settings = GoogleTranslatorSettings.getInstance()
  private val composePanel = ComposePanel()

  init {
    title = "Google Translator Settings"
    composePanel.setContent {
      IdeTheme {
        SettingsContent()
      }
    }
    init()
  }

  override fun createCenterPanel(): JComponent = composePanel

  @Composable
  private fun SettingsContent() {
    var useCustomServer by remember { mutableStateOf(settings.useCustomServer) }
    var serverUrl by remember { mutableStateOf(settings.serverUrl) }
    val toggleInteraction = remember { MutableInteractionSource() }

    Column(
      modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .toggleable(
            value = useCustomServer,
            interactionSource = toggleInteraction,
            indication = null,
            role = Role.Checkbox,
            onValueChange = {
              useCustomServer = it
              if (!it) {
                serverUrl = settings.serverUrl
              }
            }
          ),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        IdeCheckbox(checked = useCustomServer)
        Text(
          text = "Use custom server",
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurface
        )
      }

      Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
          text = "Server URL",
          style = MaterialTheme.typography.labelMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        IdeTextField(
          modifier = Modifier.fillMaxWidth(),
          value = serverUrl,
          onValueChange = { serverUrl = it.trimStart() },
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

      Text(
        text = "Defaults to translate.googleapis.com when not specified.",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
    }

    footer = {
      settings.useCustomServer = useCustomServer
      if (useCustomServer) {
        settings.serverUrl = serverUrl.ifBlank { GoogleTranslatorSettings.DEFAULT_SERVER_URL }
      }
    }
  }

  private var footer: (() -> Unit)? = null

  override fun doOKAction() {
    footer?.invoke()
    super.doOKAction()
  }
}
