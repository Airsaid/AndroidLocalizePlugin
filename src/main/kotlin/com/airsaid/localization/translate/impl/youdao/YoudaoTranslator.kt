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

package com.airsaid.localization.translate.impl.youdao

import com.airsaid.localization.translate.AbstractTranslator
import com.airsaid.localization.translate.TranslationException
import com.airsaid.localization.translate.TranslatorCredentialDescriptor
import com.airsaid.localization.translate.lang.Lang
import com.airsaid.localization.translate.lang.Languages
import com.airsaid.localization.translate.lang.toLang
import com.airsaid.localization.translate.util.GsonUtil
import com.google.auto.service.AutoService
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.util.Pair
import com.intellij.util.io.RequestBuilder
import icons.PluginIcons
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

/**
 * @author airsaid
 */
@Suppress("SpellCheckingInspection", "unused")
@AutoService(AbstractTranslator::class)
class YoudaoTranslator : AbstractTranslator() {
  companion object {
    private val LOG = Logger.getInstance(YoudaoTranslator::class.java)
    private const val KEY = "Youdao"
    private const val HOST_URL = "https://openapi.youdao.com"
    private const val TRANSLATE_URL = "$HOST_URL/api"
    private const val APPLY_APP_ID_URL =
      "https://ai.youdao.com/DOCSIRMA/html/%E8%87%AA%E7%84%B6%E8%AF%AD%E8%A8%80%E7%BF%BB%E8%AF%91/API%E6%96%87%E6%A1%A3/%E6%96%87%E6%9C%AC%E7%BF%BB%E8%AF%91%E6%9C%8D%E5%8A%A1/%E6%96%87%E6%9C%AC%E7%BF%BB%E8%AF%91%E6%9C%8D%E5%8A%A1-API%E6%96%87%E6%A1%A3.html"
  }

  override val key = KEY

  override val icon = PluginIcons.YOUDAO_ICON

  override val supportedLanguages: List<Lang> by lazy {
    listOf(
      Languages.CHINESE_SIMPLIFIED.toLang().setTranslationCode("zh-CHS"),
      Languages.ENGLISH.toLang(),
      Languages.JAPANESE.toLang(),
      Languages.KOREAN.toLang(),
      Languages.FRENCH.toLang(),
      Languages.SPANISH.toLang(),
      Languages.ITALIAN.toLang(),
      Languages.RUSSIAN.toLang(),
      Languages.VIETNAMESE.toLang(),
      Languages.GERMAN.toLang(),
      Languages.ARABIC.toLang(),
      Languages.INDONESIAN.toLang().setTranslationCode("id"),
      Languages.AFRIKAANS.toLang(),
      Languages.BOSNIAN.toLang(),
      Languages.BULGARIAN.toLang(),
      Languages.CATALAN.toLang(),
      Languages.CROATIAN.toLang(),
      Languages.CZECH.toLang(),
      Languages.DANISH.toLang(),
      Languages.DUTCH.toLang(),
      Languages.ESTONIAN.toLang(),
      Languages.FINNISH.toLang(),
      Languages.HAITIAN_CREOLE.toLang(),
      Languages.HINDI.toLang(),
      Languages.HUNGARIAN.toLang(),
      Languages.SWAHILI.toLang(),
      Languages.LITHUANIAN.toLang(),
      Languages.MALAY.toLang(),
      Languages.MALTESE.toLang(),
      Languages.NORWEGIAN.toLang(),
      Languages.POLISH.toLang(),
      Languages.ROMANIAN.toLang(),
      Languages.SERBIAN.toLang().setTranslationCode("sr-Cyrl"),
      Languages.SLOVAK.toLang(),
      Languages.SLOVENIAN.toLang(),
      Languages.SWEDISH.toLang(),
      Languages.THAI.toLang(),
      Languages.TURKISH.toLang(),
      Languages.UKRAINIAN.toLang(),
      Languages.URDU.toLang(),
      Languages.AMHARIC.toLang(),
      Languages.AZERBAIJANI.toLang(),
      Languages.BANGLA.toLang(),
      Languages.BASQUE.toLang(),
      Languages.BELARUSIAN.toLang(),
      Languages.CEBUANO.toLang(),
      Languages.CORSICAN.toLang(),
      Languages.ESPERANTO.toLang(),
      Languages.FILIPINO.toLang().setTranslationCode("tl"),
      Languages.FRISIAN.toLang(),
      Languages.GUJARATI.toLang(),
      Languages.HAUSA.toLang(),
      Languages.HAWAIIAN.toLang(),
      Languages.ICELANDIC.toLang(),
      Languages.JAVANESE.toLang().setTranslationCode("jw"),
      Languages.KANNADA.toLang(),
      Languages.KAZAKH.toLang(),
      Languages.KHMER.toLang(),
      Languages.KURDISH.toLang(),
      Languages.KYRGYZ.toLang(),
      Languages.LAO.toLang(),
      Languages.LATIN.toLang(),
      Languages.LUXEMBOURGISH.toLang(),
      Languages.MACEDONIAN.toLang(),
      Languages.MALAGASY.toLang(),
      Languages.MALAYALAM.toLang(),
      Languages.MARATHI.toLang(),
      Languages.MONGOLIAN.toLang(),
      Languages.BURMESE.toLang(),
      Languages.NEPALI.toLang(),
      Languages.CHICHEWA.toLang(),
      Languages.PASHTO.toLang(),
      Languages.PUNJABI.toLang(),
      Languages.SAMOAN.toLang(),
      Languages.SCOTTISH_GAELIC.toLang(),
      Languages.SOTHO.toLang(),
      Languages.SHONA.toLang(),
      Languages.SINDHI.toLang(),
      Languages.SLOVENIAN.toLang(),
      Languages.SOMALI.toLang(),
      Languages.SUNDANESE.toLang(),
      Languages.TAJIK.toLang(),
      Languages.TAMIL.toLang(),
      Languages.TELUGU.toLang(),
      Languages.UZBEK.toLang(),
      Languages.XHOSA.toLang(),
      Languages.YORUBA.toLang(),
      Languages.ZULU.toLang(),
    )
  }

  override val credentialDefinitions = listOf(
    TranslatorCredentialDescriptor(id = "appId", label = "APP ID", isSecret = false),
    TranslatorCredentialDescriptor(id = "appKey", label = "APP KEY", isSecret = true)
  )

  override val credentialHelpUrl: String? = APPLY_APP_ID_URL

  override fun getRequestUrl(fromLang: Lang, toLang: Lang, text: String): String {
    return TRANSLATE_URL
  }

  private fun truncate(q: String): String {
    val len = q.length
    return if (len <= 20) q else (q.substring(0, 10) + len + q.substring(len - 10, len))
  }

  private fun getDigest(string: String): String? {
    val hexDigits = charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F')
    val btInput = string.toByteArray(StandardCharsets.UTF_8)
    return try {
      val mdInst = MessageDigest.getInstance("SHA-256")
      mdInst.update(btInput)
      val md = mdInst.digest()
      val j = md.size
      val str = CharArray(j * 2)
      var k = 0
      for (byte0 in md) {
        str[k++] = hexDigits[byte0.toInt() ushr 4 and 0xf]
        str[k++] = hexDigits[byte0.toInt() and 0xf]
      }
      String(str)
    } catch (e: NoSuchAlgorithmException) {
      null
    }
  }

  override fun getRequestParams(fromLang: Lang, toLang: Lang, text: String): List<Pair<String, String>> {
    val salt = System.currentTimeMillis().toString()
    val curTime = (System.currentTimeMillis() / 1000).toString()
    val appId = credentialValue("appId")
    val appKey = credentialValue("appKey")
    val sign = getDigest(appId + truncate(text) + salt + curTime + appKey)
    val params = mutableListOf<Pair<String, String>>()
    params.add(Pair.create("from", fromLang.translationCode))
    params.add(Pair.create("to", toLang.translationCode))
    params.add(Pair.create("signType", "v3"))
    params.add(Pair.create("curtime", curTime))
    params.add(Pair.create("appKey", appId))
    params.add(Pair.create("salt", salt))
    params.add(Pair.create("sign", sign))
    params.add(Pair.create("q", text))
    return params
  }

  override fun configureRequestBuilder(requestBuilder: RequestBuilder) {
    requestBuilder.tuner { connection ->
      connection.setRequestProperty("Referer", HOST_URL)
    }
  }

  override fun parsingResult(fromLang: Lang, toLang: Lang, text: String, resultText: String): String {
    LOG.info("parsingResult: $resultText")
    val translationResult = GsonUtil.getInstance().gson.fromJson(resultText, YoudaoTranslationResult::class.java)
    return if (translationResult.isSuccess) {
      translationResult.translationResult
    } else {
      throw TranslationException(fromLang, toLang, text, translationResult.errorCode ?: "Unknown error")
    }
  }
}
