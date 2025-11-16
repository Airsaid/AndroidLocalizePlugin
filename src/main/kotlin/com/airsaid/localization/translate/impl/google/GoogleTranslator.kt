package com.airsaid.localization.translate.impl.google

import com.airsaid.localization.translate.AbstractTranslator
import com.airsaid.localization.translate.TranslationException
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
import javax.swing.Icon

/**
 * Translator implementation that proxies requests through the Google translate web endpoint.
 *
 * @author airsaid
 */
@AutoService(AbstractTranslator::class)
class GoogleTranslator : AbsGoogleTranslator() {

  private val log = Logger.getInstance(GoogleTranslator::class.java)

  private val useCustomApiKey: Boolean
    get() = GoogleTranslatorSettings.getInstance().useCustomApiKey

  override val key: String = KEY

  override val icon: Icon = PluginIcons.GOOGLE_ICON

  override fun getRequestUrl(fromLang: Lang, toLang: Lang, text: String): String {
    if (useCustomApiKey) {
      val apiKey = credentialValue(API_KEY_DESCRIPTOR.id)
      return UrlBuilder(CLOUD_TRANSLATE_V2_URL)
        .addQueryParameter("key", apiKey)
        .build()
    }

    val source = if (fromLang.code.equals(Languages.AUTO.code, ignoreCase = true)) "auto" else fromLang.translationCode
    val builder = UrlBuilder(googleApiUrl(TRANSLATE_PATH))
      .addQueryParameter("client", "gtx")
      .addQueryParameter("sl", source)
      .addQueryParameter("tl", toLang.translationCode)
      .addQueryParameters("dt", "t", "bd", "rm", "qca", "ex")
      .addQueryParameter("dj", "1")
      .addQueryParameter("ie", "UTF-8")
      .addQueryParameter("oe", "UTF-8")
      .addQueryParameter("hl", Languages.ENGLISH.toLang().translationCode)
      .addQueryParameter("tk", text.tk())
    return builder.build()
  }

  override fun getRequestParams(fromLang: Lang, toLang: Lang, text: String): List<Pair<String, String>> {
    if (useCustomApiKey) return emptyList()

    return listOf(Pair.create("q", text))
  }

  override fun configureRequestBuilder(requestBuilder: RequestBuilder) {
    if (!useCustomApiKey) {
      requestBuilder.withGoogleHeaders()
    }
  }

  override val requestContentType: String
    get() = if (useCustomApiKey) JSON_CONTENT_TYPE else super.requestContentType

  override fun getRequestBody(fromLang: Lang, toLang: Lang, text: String): String {
    if (!useCustomApiKey) return ""

    val request = GoogleCloudTranslationRequest(
      q = listOf(text),
      target = toLang.translationCode,
      format = "text",
      source = fromLang.takeUnless { it.code.equals(Languages.AUTO.code, ignoreCase = true) }?.translationCode
    )
    return GsonUtil.getInstance().gson.toJson(request)
  }

  /**
   * Parses the JSON payload and surfaces API errors as `TranslationException`.
   */
  override fun parsingResult(fromLang: Lang, toLang: Lang, text: String, resultText: String): String {
    return if (useCustomApiKey) {
      parseCloudResponse(fromLang, toLang, text, resultText)
    } else {
      parseWebResponse(fromLang, toLang, text, resultText)
    }
  }

  companion object {
    private const val KEY = "Google"
    private const val TRANSLATE_PATH = "/translate_a/single"
    private const val CLOUD_TRANSLATE_V2_URL = "https://translation.googleapis.com/language/translate/v2"
    private const val JSON_CONTENT_TYPE = "application/json; charset=UTF-8"
  }

  private fun parseWebResponse(fromLang: Lang, toLang: Lang, text: String, resultText: String): String {
    val response = GsonUtil.getInstance().gson.fromJson(resultText, GoogleTranslationResponse::class.java)
    response.error?.message?.let { message ->
      throw TranslationException(fromLang, toLang, text, message)
    }

    val translation = response.sentences
      ?.mapNotNull { it.translation }
      ?.joinToString(separator = "")
      ?.trim()
      .orEmpty()

    if (translation.isEmpty()) {
      log.warn("Empty translation from Google API: $resultText")
      return ""
    }
    return translation
  }

  private fun parseCloudResponse(fromLang: Lang, toLang: Lang, text: String, resultText: String): String {
    val response = GsonUtil.getInstance().gson.fromJson(resultText, GoogleCloudTranslationResponse::class.java)
    response.error?.message?.let { message ->
      throw TranslationException(fromLang, toLang, text, message)
    }
    val translation = response.data?.translations
      ?.mapNotNull { it.translatedText }
      ?.joinToString(separator = "")
      ?.trim()
      .orEmpty()
    if (translation.isEmpty()) {
      log.warn("Empty translation from Google Cloud Translation API: $resultText")
      return ""
    }
    return translation
  }
}

private data class GoogleCloudTranslationRequest(
  val q: List<String>,
  val target: String,
  val format: String,
  val source: String?
)

private data class GoogleCloudTranslationResponse(
  val data: TranslationData?,
  val error: CloudError?
) {
  data class TranslationData(val translations: List<TranslationEntry>?)
  data class TranslationEntry(val translatedText: String?)
  data class CloudError(val message: String?)
}
