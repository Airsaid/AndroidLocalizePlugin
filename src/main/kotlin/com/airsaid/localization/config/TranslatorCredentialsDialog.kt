package com.airsaid.localization.config

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.airsaid.localization.translate.AbstractTranslator
import com.airsaid.localization.translate.TranslatorCredentialDescriptor
import com.airsaid.localization.ui.ComposeDialog
import com.airsaid.localization.ui.components.IdeTextField
import com.intellij.ide.BrowserUtil

class TranslatorCredentialsDialog(
  private val translator: AbstractTranslator,
  private val settingsState: SettingsState,
) : ComposeDialog() {

  private val credentialValuesState: SnapshotStateMap<String, String> = mutableStateMapOf()

  init {
    title = "${translator.name} Settings"

    translator.credentialDefinitions.forEach { descriptor ->
      credentialValuesState[descriptor.id] = settingsState.getCredential(translator.key, descriptor)
    }
  }

  @Composable
  override fun Content(onOkAction: (callback: () -> Unit) -> Unit) {
    val descriptors = remember { translator.credentialDefinitions }

    Column(
      modifier = Modifier
        .padding(horizontal = 20.dp, vertical = 16.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      descriptors.forEach { descriptor ->
        CredentialField(descriptor = descriptor)
      }

      translator.credentialHelpUrl?.takeUnless { it.isBlank() }?.let { url ->
        TextButton(onClick = { BrowserUtil.browse(url) }) {
          Text("How to obtain credentials?")
        }
      }
    }

    onOkAction {
      credentialValuesState.forEach { (id, value) ->
        translator.credentialDefinitions.firstOrNull { it.id == id }?.let { descriptor ->
          settingsState.setCredential(translator.key, descriptor, value.trim())
        }
      }
    }
  }

  @Composable
  private fun CredentialField(descriptor: TranslatorCredentialDescriptor) {
    Column(modifier = Modifier.width(320.dp)) {
      Text(
        text = descriptor.label,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
      Spacer(modifier = Modifier.height(6.dp))
      IdeTextField(
        value = credentialValuesState[descriptor.id] ?: "",
        onValueChange = { newValue -> credentialValuesState[descriptor.id] = newValue.trimStart() },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        secureInput = descriptor.isSecret
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
}