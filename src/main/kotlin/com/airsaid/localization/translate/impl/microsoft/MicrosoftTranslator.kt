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
import com.airsaid.localization.translate.lang.toLang
import com.airsaid.localization.translate.util.GsonUtil
import com.airsaid.localization.translate.util.UrlBuilder
import com.google.auto.service.AutoService
import com.intellij.openapi.diagnostic.Logger
import com.intellij.util.io.RequestBuilder
import icons.PluginIcons

/**
 * @author airsaid
 */
@AutoService(AbstractTranslator::class)
class MicrosoftTranslator : AbstractTranslator() {

  companion object {
    private val LOG = Logger.getInstance(MicrosoftTranslator::class.java)
    private const val KEY = "Microsoft"
    private const val DEFAULT_HOST_URL = "https://api.cognitive.microsofttranslator.com"

    @Volatile
    internal var hostOverride: String? = null

    private val HOST_URL: String
      get() = hostOverride ?: DEFAULT_HOST_URL

    private val TRANSLATE_URL: String
      get() = "$HOST_URL/translate"
  }

  override val key = KEY

  override val icon = PluginIcons.MICROSOFT_ICON

  override val credentialDefinitions: List<TranslatorCredentialDescriptor> = emptyList()

  override val credentialHelpUrl: String? = null

  override val supportedLanguages: List<Lang> by lazy {
    buildList {
      add(Languages.AFRIKAANS.toLang())
      add(Languages.ALBANIAN.toLang())
      add(Languages.AMHARIC.toLang())
      add(Languages.ARABIC.toLang())
      add(Languages.ARMENIAN.toLang())
      add(Languages.ASSAMESE.toLang())
      add(Languages.AZERBAIJANI.toLang())
      add(Languages.BANGLA.toLang())
      add(Languages.BOSNIAN.toLang())
      add(Languages.BULGARIAN.toLang())
      add(Languages.CATALAN.toLang())
      add(Languages.CHINESE_SIMPLIFIED.toLang().setTranslationCode("zh-Hans"))
      add(Languages.CHINESE_TRADITIONAL.toLang().setTranslationCode("zh-Hant"))
      add(Languages.CROATIAN.toLang())
      add(Languages.CZECH.toLang())
      add(Languages.DANISH.toLang())
      add(Languages.DARI.toLang())
      add(Languages.DUTCH.toLang())
      add(Languages.ENGLISH.toLang())
      add(Languages.ESTONIAN.toLang())
      add(Languages.FIJIAN.toLang())
      add(Languages.FILIPINO.toLang().setTranslationCode("fil"))
      add(Languages.FINNISH.toLang())
      add(Languages.FRENCH.toLang())
      add(Languages.GERMAN.toLang())
      add(Languages.GREEK.toLang())
      add(Languages.GUJARATI.toLang())
      add(Languages.HAITIAN_CREOLE.toLang())
      add(Languages.HEBREW.toLang().setTranslationCode("he"))
      add(Languages.HINDI.toLang())
      add(Languages.HMONG_DAW.toLang())
      add(Languages.HUNGARIAN.toLang())
      add(Languages.ICELANDIC.toLang())
      add(Languages.INDONESIAN.toLang().setTranslationCode("id"))
      add(Languages.INUKTITUT.toLang())
      add(Languages.IRISH.toLang())
      add(Languages.ITALIAN.toLang())
      add(Languages.JAPANESE.toLang())
      add(Languages.KANNADA.toLang())
      add(Languages.KAZAKH.toLang())
      add(Languages.KHMER.toLang())
      add(Languages.KOREAN.toLang())
      add(Languages.KURDISH.toLang())
      add(Languages.LAO.toLang())
      add(Languages.LATVIAN.toLang())
      add(Languages.LITHUANIAN.toLang())
      add(Languages.MALAGASY.toLang())
      add(Languages.MALAY.toLang())
      add(Languages.MALAYALAM.toLang())
      add(Languages.MALTESE.toLang())
      add(Languages.MAORI.toLang())
      add(Languages.MARATHI.toLang())
      add(Languages.BURMESE.toLang())
      add(Languages.NEPALI.toLang())
      add(Languages.NORWEGIAN.toLang().setTranslationCode("nb"))
      add(Languages.ODIA.toLang())
      add(Languages.PASHTO.toLang())
      add(Languages.PERSIAN.toLang())
      add(Languages.PORTUGUESE.toLang())
      add(Languages.PUNJABI.toLang())
      add(Languages.QUERETARO_OTOMI.toLang())
      add(Languages.ROMANIAN.toLang())
      add(Languages.RUSSIAN.toLang())
      add(Languages.SAMOAN.toLang())
      add(Languages.SERBIAN.toLang())
      add(Languages.SLOVAK.toLang())
      add(Languages.SLOVENIAN.toLang())
      add(Languages.SPANISH.toLang())
      add(Languages.SWAHILI.toLang())
      add(Languages.SWEDISH.toLang())
      add(Languages.TAHITIAN.toLang())
      add(Languages.TAMIL.toLang())
      add(Languages.TELUGU.toLang())
      add(Languages.THAI.toLang())
      add(Languages.TIGRINYA.toLang())
      add(Languages.TONGAN.toLang())
      add(Languages.TURKISH.toLang())
      add(Languages.UKRAINIAN.toLang())
      add(Languages.URDU.toLang())
      add(Languages.VIETNAMESE.toLang())
      add(Languages.WELSH.toLang())
      add(Languages.YUCATEC_MAYA.toLang())
    }
  }

  override val requestContentType = "application/json"

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
      val token = MicrosoftEdgeAuthService.getInstance().getAccessToken()
      connection.setRequestProperty("Authorization", "Bearer $token")
      connection.setRequestProperty("Content-type", "application/json")
    }
  }

  override fun parsingResult(fromLang: Lang, toLang: Lang, text: String, resultText: String): String {
    LOG.info("parsingResult: $resultText")
    return GsonUtil.getInstance().gson.fromJson(
      resultText,
      Array<MicrosoftTranslationResult>::class.java
    )[0].translationResult
  }
}
