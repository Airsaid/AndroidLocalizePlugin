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

package com.airsaid.localization.translate.impl.ali

import com.airsaid.localization.translate.AbstractTranslator
import com.airsaid.localization.translate.TranslationException
import com.airsaid.localization.translate.TranslatorCredentialDescriptor
import com.airsaid.localization.translate.lang.Lang
import com.airsaid.localization.translate.lang.Languages
import com.aliyun.alimt20181012.Client
import com.aliyun.alimt20181012.models.TranslateGeneralRequest
import com.aliyun.alimt20181012.models.TranslateGeneralResponse
import com.aliyun.teaopenapi.models.Config
import com.aliyun.teautil.models.RuntimeOptions
import com.google.auto.service.AutoService
import icons.PluginIcons
import javax.swing.Icon

/**
 * @author airsaid
 */
@AutoService(AbstractTranslator::class)
class AliTranslator : AbstractTranslator() {

    companion object {
        private const val KEY = "Ali"
        private const val ENDPOINT = "mt.aliyuncs.com"
        private const val APPLY_APP_ID_URL = "https://www.aliyun.com/product/ai/base_alimt"
    }

    private var _supportedLanguages: MutableList<Lang>? = null

    override val key: String = KEY

    override val name: String = "Ali"

    override val icon: Icon? = PluginIcons.ALI_ICON

    override val credentialDefinitions = listOf(
        TranslatorCredentialDescriptor(id = "appId", label = "AccessKey ID", isSecret = false),
        TranslatorCredentialDescriptor(id = "appKey", label = "AccessKey Secret", isSecret = true)
    )

    override val credentialHelpUrl: String? = APPLY_APP_ID_URL

    override val supportedLanguages: List<Lang>
        get() {
        if (_supportedLanguages == null) {
            _supportedLanguages = mutableListOf<Lang>().apply {
                val languages = Languages.getLanguages()
                for (i in 1 until languages.size) {
                    var lang = languages[i]
                    if (lang == Languages.UKRAINIAN || lang == Languages.DARI) {
                        continue
                    }

                    lang = when (lang) {
                        Languages.CHINESE_SIMPLIFIED -> lang.setTranslationCode("zh")
                        Languages.CHINESE_TRADITIONAL -> lang.setTranslationCode("zh-tw")
                        Languages.INDONESIAN -> lang.setTranslationCode("id")
                        Languages.CROATIAN -> lang.setTranslationCode("hbs")
                        Languages.HEBREW -> lang.setTranslationCode("he")
                        else -> lang
                    }
                    add(lang)
                }
            }
        }
        return _supportedLanguages!!
    }

    @Throws(TranslationException::class)
    override fun doTranslate(fromLang: Lang, toLang: Lang, text: String): String {
        checkSupportedLanguages(fromLang, toLang, text)

        val credentials = resolveCredentials(fromLang, toLang, text)

        val config = Config()
            .setAccessKeyId(credentials.first)
            .setAccessKeySecret(credentials.second)
            .setEndpoint(ENDPOINT)
        val client = try {
            Client(config)
        } catch (e: Exception) {
            throw TranslationException(fromLang, toLang, text, e)
        }

        val request = TranslateGeneralRequest()
            .setFormatType("text")
            .setSourceLanguage(fromLang.translationCode)
            .setTargetLanguage(toLang.translationCode)
            .setSourceText(text)
            .setScene("general")

        val runtime = RuntimeOptions()
        val response: TranslateGeneralResponse

        try {
            response = client.translateGeneralWithOptions(request, runtime)
        } catch (e: Exception) {
            throw TranslationException(fromLang, toLang, text, e)
        }

        val body = response.body
        return if (body.code == 200) {
            body.data.translated
        } else {
            throw TranslationException(fromLang, toLang, text, "${body.message}(${body.code})")
        }
    }
    private fun resolveCredentials(
        fromLang: Lang,
        toLang: Lang,
        text: String
    ): Pair<String, String> {
        val accessKeyId = credentialValue("appId").takeIf { it.isNotBlank() }
        val accessKeySecret = credentialValue("appKey").takeIf { it.isNotBlank() }
        if (accessKeyId == null || accessKeySecret == null) {
            throw TranslationException(fromLang, toLang, text, "AccessKey credentials are not configured")
        }
        return Pair(accessKeyId, accessKeySecret)
    }
}
