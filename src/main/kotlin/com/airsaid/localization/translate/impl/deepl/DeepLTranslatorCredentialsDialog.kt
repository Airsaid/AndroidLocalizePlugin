package com.airsaid.localization.translate.impl.deepl

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.airsaid.localization.config.SettingsState
import com.airsaid.localization.config.TranslatorCredentialsDialog
import com.airsaid.localization.translate.AbstractTranslator
import com.airsaid.localization.ui.components.IdeCheckBox

class DeepLTranslatorCredentialsDialog(
  translator: AbstractTranslator,
  settingsState: SettingsState,
) : TranslatorCredentialsDialog(translator, settingsState) {

  private val deeplSettings = DeepLTranslatorSettings.getInstance()
  private var useDeepLPro by mutableStateOf(deeplSettings.usePro)

  @Composable
  override fun Header() {
    IdeCheckBox(
      checked = useDeepLPro,
      onValueChange = {
        useDeepLPro = !useDeepLPro
      },
      title = "Use DeepL Pro",
      subTitle = "Route requests through the paid DeepL API endpoint."
    )
  }

  override fun doOKAction() {
    super.doOKAction()
    deeplSettings.usePro = useDeepLPro
  }
}
