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
import com.airsaid.localization.translate.util.GsonUtil
import com.airsaid.localization.translate.util.UrlBuilder
import com.google.auto.service.AutoService
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.util.Pair
import com.intellij.util.io.RequestBuilder
import icons.PluginIcons
import javax.swing.Icon

/**
 * @author musagil
 */
@AutoService(AbstractTranslator::class)
open class DeepLTranslator : AbstractTranslator() {

    companion object {
        private val LOG = Logger.getInstance(DeepLTranslator::class.java)
        private const val KEY = "DeepL"
        private const val HOST_URL = "https://api-free.deepl.com/v2"
        private const val TRANSLATE_URL = "$HOST_URL/translate"
        private const val APPLY_APP_ID_URL = "https://www.deepl.com/pro-api?cta=header-pro-api/"
    }

    private var _supportedLanguages: MutableList<Lang>? = null

    override val key: String = KEY

    override val name: String = "DeepL"

    override val icon: Icon? = PluginIcons.DEEP_L_ICON

    override val credentialDefinitions = listOf(
        TranslatorCredentialDescriptor(id = "appKey", label = "KEY", isSecret = true)
    )

    override val credentialHelpUrl: String? = APPLY_APP_ID_URL

    override val supportedLanguages: List<Lang>
        get() {
            if (_supportedLanguages == null) {
                _supportedLanguages = mutableListOf<Lang>().apply {
                    add(Languages.BULGARIAN)
                    add(Languages.CZECH)
                    add(Languages.DANISH)
                    add(Languages.GERMAN)
                    add(Languages.GREEK)
                    add(Lang(118, "en-gb", "English (British)", "English (British)"))
                    add(Lang(119, "en-us", "English (American)", "English (American)"))
                    add(Languages.SPANISH)
                    add(Languages.ESTONIAN)
                    add(Languages.FINNISH)
                    add(Languages.FRENCH)
                    add(Languages.HUNGARIAN)
                    add(Lang(98, "id", "Indonesia", "Indonesian"))
                    add(Languages.ITALIAN)
                    add(Languages.JAPANESE)
                    add(Languages.KOREAN.setTranslationCode("KO"))
                    add(Languages.LITHUANIAN)
                    add(Languages.LATVIAN)
                    add(Languages.NORWEGIAN.setTranslationCode("NB"))
                    add(Languages.DUTCH)
                    add(Languages.POLISH)
                    add(Lang(120, "pt-br", "Portuguese (Brazilian)", "Portuguese (Brazilian)"))
                    add(Lang(121, "pt-pt", "Portuguese (European)", "Portuguese (European)"))
                    add(Languages.ROMANIAN)
                    add(Languages.RUSSIAN)
                    add(Languages.SLOVAK)
                    add(Languages.SLOVENIAN)
                    add(Languages.SWEDISH)
                    add(Languages.TURKISH)
                    add(Languages.UKRAINIAN)
                    add(Lang(104, "zh", "简体中文", "Chinese Simplified"))
                }
            }
            return _supportedLanguages!!
        }

    override fun getRequestUrl(fromLang: Lang, toLang: Lang, text: String): String =
        UrlBuilder(TRANSLATE_URL).build()

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
