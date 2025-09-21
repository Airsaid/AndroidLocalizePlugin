/*
 * Copyright 2021 Airsaid. https://github.com/airsaid
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.airsaid.localization.translate.impl.microsoft

import com.airsaid.localization.translate.AbstractTranslator
import com.airsaid.localization.translate.TranslatorCredentialDescriptor
import com.airsaid.localization.translate.lang.Lang
import com.airsaid.localization.translate.lang.Languages
import com.airsaid.localization.translate.util.GsonUtil
import com.airsaid.localization.translate.util.UrlBuilder
import com.google.auto.service.AutoService
import com.intellij.openapi.diagnostic.Logger
import com.intellij.util.io.RequestBuilder
import icons.PluginIcons
import javax.swing.Icon

/**
 * @author airsaid
 */
@AutoService(AbstractTranslator::class)
class MicrosoftTranslator : AbstractTranslator() {

    companion object {
        private val LOG = Logger.getInstance(MicrosoftTranslator::class.java)
        private const val KEY = "Microsoft"
        private const val HOST_URL = "https://api.cognitive.microsofttranslator.com"
        private const val TRANSLATE_URL = "$HOST_URL/translate"
        private const val APPLY_APP_ID_URL = "https://docs.microsoft.com/azure/cognitive-services/translator/translator-how-to-signup"
    }

    private var _supportedLanguages: MutableList<Lang>? = null

    override val key: String = KEY

    override val name: String = "Microsoft"

    override val icon: Icon? = PluginIcons.MICROSOFT_ICON

    override val credentialDefinitions = listOf(
        TranslatorCredentialDescriptor(id = "appKey", label = "KEY", isSecret = true)
    )

    override val credentialHelpUrl: String? = APPLY_APP_ID_URL

    override val supportedLanguages: List<Lang>
        get() {
        if (_supportedLanguages == null) {
            _supportedLanguages = mutableListOf<Lang>().apply {
                add(Languages.AFRIKAANS)
                add(Languages.ALBANIAN)
                add(Languages.AMHARIC)
                add(Languages.ARABIC)
                add(Languages.ARMENIAN)
                add(Languages.ASSAMESE)
                add(Languages.AZERBAIJANI)
                add(Languages.BANGLA)
                add(Languages.BOSNIAN)
                add(Languages.BULGARIAN)
                add(Languages.CATALAN)
                add(Languages.CHINESE_SIMPLIFIED.setTranslationCode("zh-Hans"))
                add(Languages.CHINESE_TRADITIONAL.setTranslationCode("zh-Hant"))
                add(Languages.CROATIAN)
                add(Languages.CZECH)
                add(Languages.DANISH)
                add(Languages.DARI)
                add(Languages.DUTCH)
                add(Languages.ENGLISH)
                add(Languages.ESTONIAN)
                add(Languages.FIJIAN)
                add(Languages.FILIPINO.setTranslationCode("fil"))
                add(Languages.FINNISH)
                add(Languages.FRENCH)
                add(Languages.GERMAN)
                add(Languages.GREEK)
                add(Languages.GUJARATI)
                add(Languages.HAITIAN_CREOLE)
                add(Languages.HEBREW.setTranslationCode("he"))
                add(Languages.HINDI)
                add(Languages.HMONG_DAW)
                add(Languages.HUNGARIAN)
                add(Languages.ICELANDIC)
                add(Languages.INDONESIAN.setTranslationCode("id"))
                add(Languages.INUKTITUT)
                add(Languages.IRISH)
                add(Languages.ITALIAN)
                add(Languages.JAPANESE)
                add(Languages.KANNADA)
                add(Languages.KAZAKH)
                add(Languages.KHMER)
                add(Languages.KLINGON_LATIN)
                add(Languages.KLINGON_PIQAD)
                add(Languages.KOREAN)
                add(Languages.KURDISH)
                add(Languages.LAO)
                add(Languages.LATVIAN)
                add(Languages.LITHUANIAN)
                add(Languages.MALAGASY)
                add(Languages.MALAY)
                add(Languages.MALAYALAM)
                add(Languages.MALTESE)
                add(Languages.MAORI)
                add(Languages.MARATHI)
                add(Languages.BURMESE)
                add(Languages.NEPALI)
                add(Languages.NORWEGIAN.setTranslationCode("nb"))
                add(Languages.ODIA)
                add(Languages.PASHTO)
                add(Languages.PERSIAN)
                add(Languages.PORTUGUESE)
                add(Languages.PUNJABI)
                add(Languages.QUERETARO_OTOMI)
                add(Languages.ROMANIAN)
                add(Languages.RUSSIAN)
                add(Languages.SAMOAN)
                add(Languages.SERBIAN)
                add(Languages.SLOVAK)
                add(Languages.SLOVENIAN)
                add(Languages.SPANISH)
                add(Languages.SWAHILI)
                add(Languages.SWEDISH)
                add(Languages.TAHITIAN)
                add(Languages.TAMIL)
                add(Languages.TELUGU)
                add(Languages.THAI)
                add(Languages.TIGRINYA)
                add(Languages.TONGAN)
                add(Languages.TURKISH)
                add(Languages.UKRAINIAN)
                add(Languages.URDU)
                add(Languages.VIETNAMESE)
                add(Languages.WELSH)
                add(Languages.YUCATEC_MAYA)
            }
        }
        return _supportedLanguages!!
    }

    override val requestContentType: String
        get() = "application/json"

    override fun getRequestUrl(fromLang: Lang, toLang: Lang, text: String): String {
        return UrlBuilder(TRANSLATE_URL)
            .addQueryParameter("api-version", "3.0")
            .addQueryParameter("to", toLang.translationCode)
            .build()
    }

    override fun getRequestBody(fromLang: Lang, toLang: Lang, text: String): String {
        return "[{\"Text\": \"$text\"}]"
    }

    override fun configureRequestBuilder(requestBuilder: RequestBuilder) {
        requestBuilder.tuner { connection ->
            connection.setRequestProperty("Ocp-Apim-Subscription-Key", credentialValue("appKey"))
            connection.setRequestProperty("Content-type", "application/json")
        }
    }

    override fun parsingResult(fromLang: Lang, toLang: Lang, text: String, resultText: String): String {
        LOG.info("parsingResult: $resultText")
        return GsonUtil.getInstance().gson.fromJson(resultText, Array<MicrosoftTranslationResult>::class.java)[0].translationResult
    }
}
