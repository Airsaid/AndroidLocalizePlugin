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

package com.airsaid.localization.translate.impl.baidu

import com.airsaid.localization.translate.AbstractTranslator
import com.airsaid.localization.translate.TranslationException
import com.airsaid.localization.translate.TranslatorCredentialDescriptor
import com.airsaid.localization.translate.lang.Lang
import com.airsaid.localization.translate.lang.Languages
import com.airsaid.localization.translate.lang.toLang
import com.airsaid.localization.translate.util.GsonUtil
import com.airsaid.localization.translate.util.MD5
import com.google.auto.service.AutoService
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.util.Pair
import com.intellij.util.io.RequestBuilder
import icons.PluginIcons

/**
 * @author airsaid
 */
@AutoService(AbstractTranslator::class)
class BaiduTranslator : AbstractTranslator() {

  companion object {
    private val LOG = Logger.getInstance(BaiduTranslator::class.java)
    private const val KEY = "Baidu"
    private const val HOST_URL = "http://api.fanyi.baidu.com"
    private const val TRANSLATE_URL = "$HOST_URL/api/trans/vip/translate"
    private const val APPLY_APP_ID_URL = "http://api.fanyi.baidu.com/api/trans/product/desktop?req=developer"
  }

  override val key = KEY

  override val icon = PluginIcons.BAIDU_ICON

  override val supportedLanguages: List<Lang> by lazy {
    listOf(
      Languages.CHINESE_SIMPLIFIED.toLang().setTranslationCode("zh"),
      Languages.ENGLISH.toLang(),
      Languages.JAPANESE.toLang().setTranslationCode("jp"),
      Languages.KOREAN.toLang().setTranslationCode("kor"),
      Languages.FRENCH.toLang().setTranslationCode("fra"),
      Languages.SPANISH.toLang().setTranslationCode("spa"),
      Languages.THAI.toLang(),
      Languages.ARABIC.toLang().setTranslationCode("ara"),
      Languages.RUSSIAN.toLang(),
      Languages.PORTUGUESE.toLang(),
      Languages.GERMAN.toLang(),
      Languages.ITALIAN.toLang(),
      Languages.GREEK.toLang(),
      Languages.DUTCH.toLang(),
      Languages.POLISH.toLang(),
      Languages.BULGARIAN.toLang().setTranslationCode("bul"),
      Languages.ESTONIAN.toLang().setTranslationCode("est"),
      Languages.DANISH.toLang().setTranslationCode("dan"),
      Languages.FINNISH.toLang().setTranslationCode("fin"),
      Languages.CZECH.toLang(),
      Languages.ROMANIAN.toLang().setTranslationCode("rom"),
      Languages.SLOVENIAN.toLang().setTranslationCode("slo"),
      Languages.SWEDISH.toLang().setTranslationCode("swe"),
      Languages.HUNGARIAN.toLang(),
      Languages.CHINESE_TRADITIONAL.toLang().setTranslationCode("cht"),
      Languages.VIETNAMESE.toLang().setTranslationCode("vie"),
    )
  }

  override val credentialDefinitions = listOf(
    TranslatorCredentialDescriptor(id = "appId", label = "APP ID", isSecret = false),
    TranslatorCredentialDescriptor(id = "appKey", label = "APP KEY", isSecret = true)
  )

  override val credentialHelpUrl: String? = APPLY_APP_ID_URL

  override fun getRequestUrl(fromLang: Lang, toLang: Lang, text: String): String = TRANSLATE_URL

  override fun getRequestParams(fromLang: Lang, toLang: Lang, text: String): List<Pair<String, String>> {
    val salt = System.currentTimeMillis().toString()
    val appId = credentialValue("appId")
    val securityKey = credentialValue("appKey")
    val sign = MD5.md5("$appId$text$salt$securityKey")

    return listOf(
      Pair.create("from", fromLang.translationCode),
      Pair.create("to", toLang.translationCode),
      Pair.create("appid", appId),
      Pair.create("salt", salt),
      Pair.create("sign", sign),
      Pair.create("q", text)
    )
  }

  override fun configureRequestBuilder(requestBuilder: RequestBuilder) {
    requestBuilder.tuner { connection ->
      connection.setRequestProperty("Referer", HOST_URL)
    }
  }

  override fun parsingResult(fromLang: Lang, toLang: Lang, text: String, resultText: String): String {
    LOG.info("parsingResult: $resultText")
    val baiduTranslationResult = GsonUtil.getInstance().gson.fromJson(resultText, BaiduTranslationResult::class.java)
    return if (baiduTranslationResult.isSuccess()) {
      baiduTranslationResult.translationResult
    } else {
      val message = "${baiduTranslationResult.errorMsg}(${baiduTranslationResult.errorCode})"
      throw TranslationException(fromLang, toLang, text, message)
    }
  }
}
