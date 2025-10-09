package com.airsaid.localization.config

import androidx.compose.foundation.layout.*
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
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.ui.component.Link
import org.jetbrains.jewel.ui.component.Text

/**
 * Base Compose dialog for collecting and persisting translator credentials.
 *
 * @author airsaid
 */
open class TranslatorCredentialsDialog(
  protected val translator: AbstractTranslator,
  protected val settingsState: SettingsState,
) : ComposeDialog() {

  protected val credentialValuesState: SnapshotStateMap<String, String> = mutableStateMapOf()

  init {
    title = "${translator.name} Settings"

    translator.credentialDefinitions.forEach { descriptor ->
      credentialValuesState[descriptor.id] = settingsState.getCredential(translator.key, descriptor)
    }
  }

  @Composable
  override fun Content() {
    val descriptors = remember { translator.credentialDefinitions }

    Column(
      modifier = Modifier
        .padding(horizontal = 20.dp, vertical = 16.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      Header()

      descriptors.forEach { descriptor ->
        CredentialField(descriptor = descriptor)
      }

      translator.credentialHelpUrl?.takeUnless { it.isBlank() }?.let { url ->
        Link(
          text = "How to obtain credentials?",
          onClick = { BrowserUtil.browse(url) }
        )
      }

      Footer()
    }
  }

  @Composable
  private fun CredentialField(descriptor: TranslatorCredentialDescriptor) {
    Column(modifier = Modifier.width(320.dp)) {
      Text(
        text = descriptor.label,
        color = JewelTheme.globalColors.text.info
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
          color = JewelTheme.globalColors.text.info
        )
      }
    }
  }

  /**
   * Persists the sanitized credential values before closing the dialog.
   */
  override fun doOKAction() {
    super.doOKAction()
    credentialValuesState.forEach { (id, value) ->
      translator.credentialDefinitions.firstOrNull { it.id == id }?.let { descriptor ->
        settingsState.setCredential(translator.key, descriptor, value.trim())
      }
    }
  }

  @Composable
  protected open fun Header() = Unit

  @Composable
  protected open fun Footer() = Unit
}