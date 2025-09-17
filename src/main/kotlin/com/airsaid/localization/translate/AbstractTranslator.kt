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

package com.airsaid.localization.translate

import com.airsaid.localization.config.SettingsState
import com.airsaid.localization.translate.lang.Lang
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.util.Pair
import com.intellij.util.io.HttpRequests
import com.intellij.util.io.RequestBuilder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import javax.swing.Icon

/**
 * @author airsaid
 */
abstract class AbstractTranslator : Translator, TranslatorConfigurable {

  abstract override val key: String
  abstract override val name: String

  companion object {
    protected val LOG = Logger.getInstance(AbstractTranslator::class.java)
    private const val CONTENT_TYPE = "application/x-www-form-urlencoded"
  }

  @Throws(TranslationException::class)
  override fun doTranslate(fromLang: Lang, toLang: Lang, text: String): String {
    checkSupportedLanguages(fromLang, toLang, text)

    val requestUrl = getRequestUrl(fromLang, toLang, text)
    val requestBuilder = HttpRequests.post(requestUrl, CONTENT_TYPE)
    // Set the timeout time to 60 seconds.
    requestBuilder.connectTimeout(60 * 1000)
    configureRequestBuilder(requestBuilder)

    return try {
      requestBuilder.connect { request ->
        val requestParams = getRequestParams(fromLang, toLang, text)
          .joinToString("&") { pair ->
            "${pair.first}=${URLEncoder.encode(pair.second, StandardCharsets.UTF_8)}"
          }
        if (requestParams.isNotEmpty()) {
          request.write(requestParams)
        }
        val requestBody = getRequestBody(fromLang, toLang, text)
        if (requestBody.isNotEmpty()) {
          request.write(requestBody)
        }

        val resultText = request.readString()
        parsingResult(fromLang, toLang, text, resultText)
      }
    } catch (e: Exception) {
      e.printStackTrace()
      LOG.error(e.message, e)
      throw TranslationException(fromLang, toLang, text, e)
    }
  }

  override val icon: Icon? = null

  override val isNeedAppId: Boolean = true

  override val appId: String?
    get() = SettingsState.getInstance().getAppId(key)

  override val appIdDisplay: String = "APP ID"

  override val isNeedAppKey: Boolean = true

  override val appKey: String?
    get() = SettingsState.getInstance().getAppKey(key)

  override val appKeyDisplay: String = "APP KEY"

  override val applyAppIdUrl: String? = null

  protected open fun getRequestUrl(fromLang: Lang, toLang: Lang, text: String): String {
    throw UnsupportedOperationException()
  }

  protected open fun getRequestParams(fromLang: Lang, toLang: Lang, text: String): List<Pair<String, String>> {
    return emptyList()
  }

  protected open fun getRequestBody(fromLang: Lang, toLang: Lang, text: String): String {
    return ""
  }

  protected open fun configureRequestBuilder(requestBuilder: RequestBuilder) {
    // Default implementation does nothing
  }

  protected open fun parsingResult(fromLang: Lang, toLang: Lang, text: String, resultText: String): String {
    throw UnsupportedOperationException()
  }

  protected fun checkSupportedLanguages(fromLang: Lang, toLang: Lang, text: String) {
    if (!supportedLanguages.contains(toLang)) {
      throw TranslationException(fromLang, toLang, text, "${toLang.englishName} is not supported.")
    }
  }
}