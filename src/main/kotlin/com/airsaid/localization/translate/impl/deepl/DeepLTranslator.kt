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

package com.airsaid.localization.translate.impl.deepl

import com.airsaid.localization.translate.AbstractTranslator
import com.airsaid.localization.translate.TranslatorCredentialDescriptor
import com.airsaid.localization.translate.lang.Lang
import com.airsaid.localization.translate.lang.Languages
import com.airsaid.localization.translate.lang.toLang
import com.airsaid.localization.translate.util.GsonUtil
import com.airsaid.localization.translate.util.UrlBuilder
import com.google.auto.service.AutoService
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.util.Pair
import com.intellij.util.io.RequestBuilder
import icons.PluginIcons

/**
 * @author musagil
 */
@AutoService(AbstractTranslator::class)
open class DeepLTranslator : AbstractTranslator() {

  companion object {
    private val LOG = Logger.getInstance(DeepLTranslator::class.java)
    private const val KEY = "DeepL"
    private const val FREE_HOST_URL = "https://api-free.deepl.com/v2"
    private const val PRO_HOST_URL = "https://api.deepl.com/v2"
    private const val TRANSLATE_PATH = "/translate"
    private const val APPLY_APP_ID_URL = "https://www.deepl.com/pro-api?cta=header-pro-api/"
  }

  private val deeplSettings by lazy { DeepLTranslatorSettings.getInstance() }

  override val key = KEY

  override val icon = PluginIcons.DEEP_L_ICON

  override val credentialDefinitions = listOf(
    TranslatorCredentialDescriptor(id = "appKey", label = "KEY", isSecret = true)
  )

  override val credentialHelpUrl: String? = APPLY_APP_ID_URL

  override val supportedLanguages: List<Lang> by lazy {
    buildList {
      add(Languages.BULGARIAN.toLang())
      add(Languages.CZECH.toLang())
      add(Languages.DANISH.toLang())
      add(Languages.GERMAN.toLang())
      add(Languages.GREEK.toLang())
      add(
        Languages.ENGLISH.toLang().copy(
          id = 118,
          code = "en-gb",
          name = "English (British)",
          englishName = "English (British)",
          directoryName = "en-rGB",
        ).setTranslationCode("en-gb")
      )
      add(
        Languages.ENGLISH.toLang().copy(
          id = 119,
          code = "en-us",
          name = "English (American)",
          englishName = "English (American)",
          directoryName = "en-rUS",
        ).setTranslationCode("en-us")
      )
      add(Languages.SPANISH.toLang())
      add(Languages.ESTONIAN.toLang())
      add(Languages.FINNISH.toLang())
      add(Languages.FRENCH.toLang())
      add(Languages.HUNGARIAN.toLang())
      add(Languages.INDONESIAN.toLang())
      add(Languages.ITALIAN.toLang())
      add(Languages.JAPANESE.toLang())
      add(Languages.KOREAN.toLang().setTranslationCode("KO"))
      add(Languages.LITHUANIAN.toLang())
      add(Languages.LATVIAN.toLang())
      add(Languages.NORWEGIAN.toLang().setTranslationCode("NB"))
      add(Languages.DUTCH.toLang())
      add(Languages.POLISH.toLang())
      add(
        Languages.PORTUGUESE.toLang().copy(
          id = 120,
          code = "pt-br",
          name = "Portuguese (Brazilian)",
          englishName = "Portuguese (Brazilian)",
          directoryName = "pt-rBR",
        ).setTranslationCode("pt-br")
      )
      add(
        Languages.PORTUGUESE.toLang().copy(
          id = 121,
          code = "pt-pt",
          name = "Portuguese (European)",
          englishName = "Portuguese (European)",
          directoryName = "pt-rPT",
        ).setTranslationCode("pt-pt")
      )
      add(Languages.ROMANIAN.toLang())
      add(Languages.RUSSIAN.toLang())
      add(Languages.SLOVAK.toLang())
      add(Languages.SLOVENIAN.toLang())
      add(Languages.SWEDISH.toLang())
      add(Languages.TURKISH.toLang())
      add(Languages.UKRAINIAN.toLang())
      add(Languages.CHINESE_SIMPLIFIED.toLang().setTranslationCode("zh"))
    }
  }

  override fun getRequestUrl(fromLang: Lang, toLang: Lang, text: String): String {
    val baseUrl = if (deeplSettings.usePro) PRO_HOST_URL else FREE_HOST_URL
    return UrlBuilder(baseUrl + TRANSLATE_PATH).build()
  }

  override fun getRequestParams(fromLang: Lang, toLang: Lang, text: String): List<Pair<String, String>> {
    return listOf(
      Pair.create("text", text),
      Pair.create("target_lang", toLang.code)
    )
  }

  override fun configureRequestBuilder(requestBuilder: RequestBuilder) {
    requestBuilder.tuner { connection ->
      connection.setRequestProperty("Authorization", "DeepL-Auth-Key ${credentialValue("appKey")}")
      connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
    }
  }

  override fun parsingResult(fromLang: Lang, toLang: Lang, text: String, resultText: String): String {
    LOG.info("parsingResult: $resultText")
    return GsonUtil.getInstance().gson.fromJson(resultText, DeepLTranslationResult::class.java).translationResult
  }
}
