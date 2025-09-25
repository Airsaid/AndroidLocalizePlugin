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
import com.airsaid.localization.translate.util.GsonUtil
import com.google.auto.service.AutoService
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.util.Pair
import com.intellij.util.io.RequestBuilder
import icons.PluginIcons
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import javax.swing.Icon

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
        private const val APPLY_APP_ID_URL = "https://ai.youdao.com/DOCSIRMA/html/%E8%87%AA%E7%84%B6%E8%AF%AD%E8%A8%80%E7%BF%BB%E8%AF%91/API%E6%96%87%E6%A1%A3/%E6%96%87%E6%9C%AC%E7%BF%BB%E8%AF%91%E6%9C%8D%E5%8A%A1/%E6%96%87%E6%9C%AC%E7%BF%BB%E8%AF%91%E6%9C%8D%E5%8A%A1-API%E6%96%87%E6%A1%A3.html"
    }

    private var _supportedLanguages: List<Lang>? = null

    override val key: String
        get() = KEY

    override val name: String
        get() = "Youdao"

    override val icon: Icon?
        get() = PluginIcons.YOUDAO_ICON

    override val supportedLanguages: List<Lang>
        get() {
            if (_supportedLanguages == null) {
                _supportedLanguages = mutableListOf<Lang>().apply {
                    add(Languages.CHINESE_SIMPLIFIED.setTranslationCode("zh-CHS"))
                    add(Languages.ENGLISH)
                    add(Languages.JAPANESE)
                    add(Languages.KOREAN)
                    add(Languages.FRENCH)
                    add(Languages.SPANISH)
                    add(Languages.ITALIAN)
                    add(Languages.RUSSIAN)
                    add(Languages.VIETNAMESE)
                    add(Languages.GERMAN)
                    add(Languages.ARABIC)
                    add(Languages.INDONESIAN.setTranslationCode("id"))
                    add(Languages.AFRIKAANS)
                    add(Languages.BOSNIAN)
                    add(Languages.BULGARIAN)
                    add(Languages.CATALAN)
                    add(Languages.CROATIAN)
                    add(Languages.CZECH)
                    add(Languages.DANISH)
                    add(Languages.DUTCH)
                    add(Languages.ESTONIAN)
                    add(Languages.FINNISH)
                    add(Languages.HAITIAN_CREOLE)
                    add(Languages.HINDI)
                    add(Languages.HUNGARIAN)
                    add(Languages.SWAHILI)
                    add(Languages.LITHUANIAN)
                    add(Languages.MALAY)
                    add(Languages.MALTESE)
                    add(Languages.NORWEGIAN)
                    add(Languages.POLISH)
                    add(Languages.ROMANIAN)
                    add(Languages.SERBIAN.setTranslationCode("sr-Cyrl"))
                    add(Languages.SLOVAK)
                    add(Languages.SLOVENIAN)
                    add(Languages.SWEDISH)
                    add(Languages.THAI)
                    add(Languages.TURKISH)
                    add(Languages.UKRAINIAN)
                    add(Languages.URDU)
                    add(Languages.AMHARIC)
                    add(Languages.AZERBAIJANI)
                    add(Languages.BANGLA)
                    add(Languages.BASQUE)
                    add(Languages.BELARUSIAN)
                    add(Languages.CEBUANO)
                    add(Languages.CORSICAN)
                    add(Languages.ESPERANTO)
                    add(Languages.FILIPINO.setTranslationCode("tl"))
                    add(Languages.FRISIAN)
                    add(Languages.GUJARATI)
                    add(Languages.HAUSA)
                    add(Languages.HAWAIIAN)
                    add(Languages.ICELANDIC)
                    add(Languages.JAVANESE.setTranslationCode("jw"))
                    add(Languages.KANNADA)
                    add(Languages.KAZAKH)
                    add(Languages.KHMER)
                    add(Languages.KURDISH)
                    add(Languages.KYRGYZ)
                    add(Languages.LAO)
                    add(Languages.LATIN)
                    add(Languages.LUXEMBOURGISH)
                    add(Languages.MACEDONIAN)
                    add(Languages.MALAGASY)
                    add(Languages.MALAYALAM)
                    add(Languages.MARATHI)
                    add(Languages.MONGOLIAN)
                    add(Languages.BURMESE)
                    add(Languages.NEPALI)
                    add(Languages.CHICHEWA)
                    add(Languages.PASHTO)
                    add(Languages.PUNJABI)
                    add(Languages.SAMOAN)
                    add(Languages.SCOTTISH_GAELIC)
                    add(Languages.SOTHO)
                    add(Languages.SHONA)
                    add(Languages.SINDHI)
                    add(Languages.SLOVENIAN)
                    add(Languages.SOMALI)
                    add(Languages.SUNDANESE)
                    add(Languages.TAJIK)
                    add(Languages.TAMIL)
                    add(Languages.TELUGU)
                    add(Languages.UZBEK)
                    add(Languages.XHOSA)
                    add(Languages.YORUBA)
                    add(Languages.ZULU)
                }
            }
            return _supportedLanguages!!
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
