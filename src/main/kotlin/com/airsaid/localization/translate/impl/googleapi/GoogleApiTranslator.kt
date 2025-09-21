package com.airsaid.localization.translate.impl.googleapi

import com.airsaid.localization.translate.AbstractTranslator
import com.airsaid.localization.translate.TranslationException
import com.airsaid.localization.translate.TranslatorCredentialDescriptor
import com.airsaid.localization.translate.impl.google.AbsGoogleTranslator
import com.airsaid.localization.translate.lang.Lang
import com.airsaid.localization.translate.util.GsonUtil
import com.google.auto.service.AutoService
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.util.Pair
import com.intellij.util.io.RequestBuilder

/**
 * @author airsaid
 */
@AutoService(AbstractTranslator::class)
class GoogleApiTranslator : AbsGoogleTranslator() {

    companion object {
        private val LOG = Logger.getInstance(GoogleApiTranslator::class.java)
        private const val KEY = "GoogleApi"
        private const val HOST_URL = "https://translation.googleapis.com"
        private const val TRANSLATE_URL = "$HOST_URL/language/translate/v2"
        private const val APPLY_APP_ID_URL = "https://cloud.google.com/translate"
    }

    override val key: String = KEY

    override val name: String = "Google"

    override fun getRequestUrl(fromLang: Lang, toLang: Lang, text: String): String = TRANSLATE_URL

    override val credentialDefinitions = listOf(
        TranslatorCredentialDescriptor(id = "appKey", label = "API Key", isSecret = true)
    )

    override val credentialHelpUrl: String? = APPLY_APP_ID_URL

    override fun getRequestParams(fromLang: Lang, toLang: Lang, text: String): List<Pair<String, String>> {
        return listOf(
            Pair.create("q", text),
            Pair.create("target", toLang.translationCode),
            Pair.create("key", credentialValue("appKey")),
            Pair.create("format", "text")
        )
    }

    override fun configureRequestBuilder(requestBuilder: RequestBuilder) {
        requestBuilder.tuner { connection -> connection.setRequestProperty("Referer", HOST_URL) }
    }

    override fun parsingResult(fromLang: Lang, toLang: Lang, text: String, resultText: String): String {
        LOG.info("parsingResult: $resultText")
        val result = GsonUtil.getInstance().gson.fromJson(resultText, GoogleApiTranslationResult::class.java)
        return if (result.isSuccess()) {
            result.translationResult
        } else {
            val message = if (result.error != null) {
                "${result.error!!.message}(${result.error!!.code})"
            } else {
                "Unknown error"
            }
            throw TranslationException(fromLang, toLang, text, message)
        }
    }
}
