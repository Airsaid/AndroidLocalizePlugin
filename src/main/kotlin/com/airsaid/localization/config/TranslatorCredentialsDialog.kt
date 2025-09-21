package com.airsaid.localization.config

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposePanel
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.airsaid.localization.translate.AbstractTranslator
import com.airsaid.localization.translate.TranslatorCredentialDescriptor
import com.airsaid.localization.ui.IdeTheme
import com.intellij.ide.BrowserUtil
import com.intellij.openapi.ui.DialogWrapper
import java.awt.Dimension
import javax.swing.JComponent

class TranslatorCredentialsDialog(
  private val translator: AbstractTranslator,
  private val settingsState: SettingsState,
) : DialogWrapper(true) {

  private val credentialValuesState: SnapshotStateMap<String, String> = mutableStateMapOf()
  private val composePanel = ComposePanel()

  init {
    title = "${translator.name} Settings"

    translator.credentialDefinitions.forEach { descriptor ->
      credentialValuesState[descriptor.id] = settingsState.getCredential(translator.key, descriptor)
    }

    composePanel.preferredSize = Dimension(460, 280)
    composePanel.setContent {
      IdeTheme {
        CredentialsContent()
      }
    }

    init()
  }

  override fun createCenterPanel(): JComponent = composePanel

  @Composable
  private fun CredentialsContent() {
    val descriptors = remember { translator.credentialDefinitions }

    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
      descriptors.forEach { descriptor ->
        CredentialField(descriptor = descriptor)
        Spacer(modifier = Modifier.height(12.dp))
      }

      translator.credentialHelpUrl?.takeUnless { it.isNullOrBlank() }?.let { url ->
        TextButton(onClick = { BrowserUtil.browse(url) }) {
          Text("How to obtain credentials?")
        }
      }
    }
  }

  @Composable
  private fun CredentialField(descriptor: TranslatorCredentialDescriptor) {
    Column(modifier = Modifier.fillMaxWidth()) {
      Text(
        text = descriptor.label,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
      Spacer(modifier = Modifier.height(6.dp))
      OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        value = credentialValuesState[descriptor.id] ?: "",
        onValueChange = { newValue -> credentialValuesState[descriptor.id] = newValue.trimStart() },
        singleLine = true,
        visualTransformation = if (descriptor.isSecret) PasswordVisualTransformation() else VisualTransformation.None,
        colors = OutlinedTextFieldDefaults.colors(
          focusedContainerColor = MaterialTheme.colorScheme.surface,
          unfocusedContainerColor = MaterialTheme.colorScheme.surface,
          focusedBorderColor = MaterialTheme.colorScheme.primary,
          unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.7f),
          focusedTextColor = MaterialTheme.colorScheme.onSurface,
          unfocusedTextColor = MaterialTheme.colorScheme.onSurface
        )
      )
      descriptor.description?.takeIf { it.isNotBlank() }?.let { helper ->
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = helper,
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
    }
  }

  override fun doOKAction() {
    credentialValuesState.forEach { (id, value) ->
      translator.credentialDefinitions.firstOrNull { it.id == id }?.let { descriptor ->
        settingsState.setCredential(translator.key, descriptor, value.trim())
      }
    }
    super.doOKAction()
  }
}
