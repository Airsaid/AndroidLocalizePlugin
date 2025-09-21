package com.airsaid.localization.config

import com.airsaid.localization.translate.AbstractTranslator
import com.intellij.openapi.application.ApplicationManager

fun interface TranslatorCredentialsLoader {
    fun load(translator: AbstractTranslator, onLoaded: (credentials: Map<String, String>) -> Unit)

    companion object {
        fun default(): TranslatorCredentialsLoader = DefaultTranslatorCredentialsLoader
    }
}

private object DefaultTranslatorCredentialsLoader : TranslatorCredentialsLoader {
    private val application = ApplicationManager.getApplication()

    override fun load(translator: AbstractTranslator, onLoaded: (Map<String, String>) -> Unit) {
        application.executeOnPooledThread {
            val settingsState = SettingsState.getInstance()
            val credentials = settingsState.getCredentials(translator.key, translator.credentialDefinitions)
            application.invokeLater {
                onLoaded(credentials)
            }
        }
    }
}
