package com.airsaid.localization.config

import com.airsaid.localization.translate.AbstractTranslator
import com.intellij.openapi.application.ApplicationManager

fun interface TranslatorCredentialsLoader {
    fun load(translator: AbstractTranslator, onLoaded: (appId: String, appKey: String) -> Unit)

    companion object {
        fun default(): TranslatorCredentialsLoader = DefaultTranslatorCredentialsLoader
    }
}

private object DefaultTranslatorCredentialsLoader : TranslatorCredentialsLoader {
    private val application = ApplicationManager.getApplication()

    override fun load(translator: AbstractTranslator, onLoaded: (String, String) -> Unit) {
        application.executeOnPooledThread {
            val settingsState = SettingsState.getInstance()
            val appId = settingsState.getAppId(translator.key)
            val appKey = settingsState.getAppKey(translator.key)
            application.invokeLater {
                onLoaded(appId, appKey)
            }
        }
    }
}
