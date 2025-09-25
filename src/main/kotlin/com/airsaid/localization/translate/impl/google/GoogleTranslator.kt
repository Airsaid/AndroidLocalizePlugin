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

@AutoService(AbstractTranslator::class)
class GoogleTranslator : AbsGoogleTranslator() {

  private val log = Logger.getInstance(GoogleTranslator::class.java)

  override val key: String = KEY

  override val icon: Icon = PluginIcons.GOOGLE_ICON

  override fun getRequestUrl(fromLang: Lang, toLang: Lang, text: String): String {
    val source = if (fromLang.id == Languages.AUTO.id) "auto" else fromLang.translationCode
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
    return listOf(Pair.create("q", text))
  }

  override fun configureRequestBuilder(requestBuilder: RequestBuilder) {
    requestBuilder.withGoogleHeaders()
  }

  override fun parsingResult(fromLang: Lang, toLang: Lang, text: String, resultText: String): String {
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

  companion object {
    private const val KEY = "Google"
    private const val TRANSLATE_PATH = "/translate_a/single"
  }
}
