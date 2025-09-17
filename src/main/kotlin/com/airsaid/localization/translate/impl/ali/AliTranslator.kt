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

    private val config = Config()
    private var _supportedLanguages: MutableList<Lang>? = null
    private var client: Client? = null

    override val key: String = KEY

    override val name: String = "Ali"

    override val icon: Icon? = PluginIcons.ALI_ICON

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

    override val appIdDisplay: String = "AccessKey ID"

    override val appKeyDisplay: String = "AccessKey Secret"

    override val applyAppIdUrl: String? = APPLY_APP_ID_URL

    @Throws(TranslationException::class)
    override fun doTranslate(fromLang: Lang, toLang: Lang, text: String): String {
        checkSupportedLanguages(fromLang, toLang, text)

        config.setAccessKeyId(appId).setAccessKeySecret(appKey).setEndpoint(ENDPOINT)

        if (client == null) {
            try {
                client = Client(config)
            } catch (e: Exception) {
                throw TranslationException(fromLang, toLang, text, e)
            }
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
            response = client!!.translateGeneralWithOptions(request, runtime)
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
}