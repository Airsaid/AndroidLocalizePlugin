package com.airsaid.localization.config

import com.airsaid.localization.translate.AbstractTranslator
import com.airsaid.localization.translate.impl.deepl.DeepLTranslatorCredentialsDialog
import com.airsaid.localization.translate.impl.google.GoogleTranslatorSettingsDialog
import com.airsaid.localization.translate.impl.openai.OpenAITranslatorSettingsDialog
import com.intellij.openapi.diagnostic.Logger

object TranslatorConfigurationManager {

  private val LOG = Logger.getInstance(TranslatorConfigurationManager::class.java)

  fun showConfigurationDialog(translator: AbstractTranslator): Boolean {
    return when (translator.key) {
      "Google" -> GoogleTranslatorSettingsDialog().showAndGet()
      "OpenAI" -> OpenAITranslatorSettingsDialog(translator, SettingsState.getInstance()).showAndGet()
      "DeepL" -> DeepLTranslatorCredentialsDialog(translator, SettingsState.getInstance()).showAndGet()
      else -> showCredentialDialog(translator)
    }
  }

  fun hasConfiguration(translator: AbstractTranslator): Boolean {
    return translator.key == "Google" || translator.key == "OpenAI" || translator.credentialDefinitions.isNotEmpty()
  }

  private fun showCredentialDialog(translator: AbstractTranslator): Boolean {
    if (translator.credentialDefinitions.isEmpty()) {
      LOG.debug("Translator ${translator.key} has no configurable credentials")
      return false
    }
    val dialog = TranslatorCredentialsDialog(translator, SettingsState.getInstance())
    return dialog.showAndGet()
  }
}
