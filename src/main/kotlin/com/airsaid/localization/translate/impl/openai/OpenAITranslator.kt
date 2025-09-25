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

package com.airsaid.localization.translate.impl.openai

import com.airsaid.localization.translate.AbstractTranslator
import com.airsaid.localization.translate.TranslatorCredentialDescriptor
import com.airsaid.localization.translate.TranslationException
import com.airsaid.localization.translate.lang.Lang
import com.airsaid.localization.translate.lang.Languages
import com.airsaid.localization.translate.util.GsonUtil
import com.google.auto.service.AutoService
import com.intellij.openapi.diagnostic.Logger
import com.intellij.util.io.RequestBuilder
import icons.PluginIcons
import javax.swing.Icon

@AutoService(AbstractTranslator::class)
class OpenAITranslator : AbstractTranslator() {
  companion object {
    private val LOG = Logger.getInstance(OpenAITranslator::class.java)
    private const val KEY = "OpenAI"
    private const val CHAT_COMPLETIONS_PATH = "/v1/chat/completions"
  }

  private val settings: OpenAITranslatorSettings
    get() = OpenAITranslatorSettings.getInstance()

  override val key = KEY

  override val name = KEY

  override val icon = PluginIcons.OPENAI_ICON

  override val credentialDefinitions: List<TranslatorCredentialDescriptor>
    get() = listOf(
      TranslatorCredentialDescriptor(id = "appKey", label = "API Key", isSecret = true)
    )

  override val supportedLanguages: List<Lang>
    get() = Languages.getLanguages()

  override val requestContentType: String
    get() = "application/json"

  @Throws(TranslationException::class)
  override fun doTranslate(fromLang: Lang, toLang: Lang, text: String): String {
    if (credentialValue("appKey").isBlank()) {
      throw TranslationException(fromLang, toLang, text, "OpenAI API key is required. Add it in OpenAI Settings.")
    }
    return super.doTranslate(fromLang, toLang, text)
  }

  override fun getRequestUrl(fromLang: Lang, toLang: Lang, text: String): String {
    return buildUrl(settings.resolvedBaseUrl(), CHAT_COMPLETIONS_PATH)
  }

  override fun getRequestBody(fromLang: Lang, toLang: Lang, text: String): String {
    val lang = toLang.englishName
    val roleSystem = String.format(
      "Translate the user provided text into high quality, well written %s. Apply these 4 translation rules; 1.Keep the exact original formatting and style, 2.Keep translations concise and just repeat the original text for unchanged translations (e.g. 'OK'), 3.Audience: native %s speakers, 4.Text can be used in Android app UI (limited space, concise translations!).",
      lang, lang
    )

    val role = OpenAIMessage("system", roleSystem)
    val msg = OpenAIMessage("user", String.format("Text to translate: %s", text))

    val body = OpenAIRequest(settings.resolvedModel(), listOf(role, msg))

    return GsonUtil.getInstance().gson.toJson(body)
  }

  override fun configureRequestBuilder(requestBuilder: RequestBuilder) {
    val apiKey = credentialValue("appKey").trim()
    requestBuilder.tuner { connection ->
      connection.setRequestProperty("Authorization", "Bearer $apiKey")
      connection.setRequestProperty("Content-Type", "application/json")
    }
  }

  override fun parsingResult(fromLang: Lang, toLang: Lang, text: String, resultText: String): String {
    LOG.info("parsingResult OpenAI: $resultText")
    return GsonUtil.getInstance().gson.fromJson(resultText, OpenAIResponse::class.java).translation
  }

  private fun buildUrl(base: String, path: String): String {
    val prefix = base.trimEnd('/')
    val suffix = path.trimStart('/')
    return "$prefix/$suffix"
  }
}
