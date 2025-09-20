package com.airsaid.localization.config

import com.airsaid.localization.translate.AbstractTranslator
import com.airsaid.localization.translate.lang.Lang
import com.intellij.openapi.application.ApplicationManager
import com.intellij.testFramework.junit5.TestApplication
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

@TestApplication
class SettingsComponentTest {

    @Test
    fun `loads credentials via loader without touching password safe on EDT`() {
        val loader = RecordingCredentialsLoader()

        ApplicationManager.getApplication().invokeAndWait {
            val component = SettingsComponent(loader)
            component.setTranslators(mapOf(TEST_TRANSLATOR.key to TEST_TRANSLATOR))
            component.setSelectedTranslator(TEST_TRANSLATOR)

            assertTrue(loader.loadCalledOnEdt, "Credentials loader should be triggered on EDT")

            loader.complete("test-app-id", "test-app-key")

            assertEquals("test-app-id", component.appId)
            assertEquals("test-app-key", component.appKey)
        }
    }

    private class RecordingCredentialsLoader : TranslatorCredentialsLoader {
        @Volatile
        var loadCalledOnEdt: Boolean = false
        private var callback: ((String, String) -> Unit)? = null

        override fun load(translator: AbstractTranslator, onLoaded: (String, String) -> Unit) {
            loadCalledOnEdt = ApplicationManager.getApplication().isDispatchThread
            callback = onLoaded
        }

        fun complete(appId: String, appKey: String) {
            ApplicationManager.getApplication().invokeAndWait {
                callback?.invoke(appId, appKey)
            }
        }
    }

    private companion object {
        val TEST_TRANSLATOR: AbstractTranslator = object : AbstractTranslator() {
            override val key: String = "test"
            override val name: String = "Test"
            override val supportedLanguages: List<Lang> = emptyList()

            override fun getRequestUrl(fromLang: Lang, toLang: Lang, text: String): String =
                throw UnsupportedOperationException()

            override fun parsingResult(
                fromLang: Lang,
                toLang: Lang,
                text: String,
                resultText: String,
            ): String = throw UnsupportedOperationException()
        }
    }
}
