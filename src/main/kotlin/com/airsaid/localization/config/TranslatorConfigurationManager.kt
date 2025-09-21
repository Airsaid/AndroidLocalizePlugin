package com.airsaid.localization.config

import com.airsaid.localization.translate.AbstractTranslator
import com.airsaid.localization.translate.impl.google.GoogleTranslatorSettingsDialog
import com.intellij.openapi.diagnostic.Logger

object TranslatorConfigurationManager {

  private val LOG = Logger.getInstance(TranslatorConfigurationManager::class.java)

  fun showConfigurationDialog(translator: AbstractTranslator): Boolean {
    return when (translator.key) {
      "Google" -> GoogleTranslatorSettingsDialog().showAndGet()
      else -> showCredentialDialog(translator)
    }
  }

  fun hasConfiguration(translator: AbstractTranslator): Boolean {
    return translator.key == "Google" || translator.credentialDefinitions.isNotEmpty()
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
