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
import com.airsaid.localization.translate.lang.Languages
import com.airsaid.localization.translate.util.HttpRequestFactory
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.util.Pair
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

  override val supportedLanguages: List<Lang>
    get() = Languages.getAllSupportedLanguages()

  companion object {
    protected val LOG = Logger.getInstance(AbstractTranslator::class.java)
    private const val DEFAULT_CONTENT_TYPE = "application/x-www-form-urlencoded"
    private const val DEFAULT_TIMEOUT_MS = 60 * 1000
  }

  @Throws(TranslationException::class)
  override fun doTranslate(fromLang: Lang, toLang: Lang, text: String): String {
    checkSupportedLanguages(fromLang, toLang, text)

    val requestUrl = getRequestUrl(fromLang, toLang, text)
    val requestBuilder = createRequestBuilder(requestUrl)
    configureRequestBuilder(requestBuilder)

    return try {
      val payload = buildRequestPayload(fromLang, toLang, text)
      requestBuilder.connect { request ->
        payload.form?.let { request.write(it) }
        payload.body?.let { body ->
          if (payload.form != null && body.isNotEmpty()) {
            request.write("&")
          }
          request.write(body)
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

  protected open fun createRequestBuilder(requestUrl: String): RequestBuilder {
    return HttpRequestFactory.post(requestUrl, requestContentType, requestTimeoutMs)
  }

  override val icon: Icon? = null

  override val credentialDefinitions: List<TranslatorCredentialDescriptor> = listOf(
    TranslatorCredentialDescriptor(id = "appId", label = "APP ID", isSecret = false),
    TranslatorCredentialDescriptor(id = "appKey", label = "APP KEY", isSecret = true),
  )

  override val credentialHelpUrl: String? = null

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

  protected open val requestContentType: String
    get() = DEFAULT_CONTENT_TYPE

  protected open val requestTimeoutMs: Int
    get() = DEFAULT_TIMEOUT_MS

  protected open fun parsingResult(fromLang: Lang, toLang: Lang, text: String, resultText: String): String {
    throw UnsupportedOperationException()
  }

  protected fun credentialValue(credentialId: String): String {
    val descriptor = credentialDefinitions.firstOrNull { it.id == credentialId }
      ?: error("Unknown credential $credentialId for translator $key")
    return SettingsState.getInstance().getCredential(key, descriptor)
  }

  protected fun checkSupportedLanguages(fromLang: Lang, toLang: Lang, text: String) {
    if (!supportedLanguages.contains(toLang)) {
      throw TranslationException(fromLang, toLang, text, "${toLang.englishName} is not supported.")
    }
  }

  private fun buildRequestPayload(
    fromLang: Lang,
    toLang: Lang,
    text: String,
  ): RequestPayload {
    val requestParams = getRequestParams(fromLang, toLang, text)
      .takeIf { it.isNotEmpty() }
      ?.joinToString("&") { pair ->
        "${pair.first}=${URLEncoder.encode(pair.second, StandardCharsets.UTF_8)}"
      }

    val requestBody = getRequestBody(fromLang, toLang, text)
      .takeIf { it.isNotEmpty() }

    return RequestPayload(requestParams, requestBody)
  }
}

private data class RequestPayload(val form: String?, val body: String?)
