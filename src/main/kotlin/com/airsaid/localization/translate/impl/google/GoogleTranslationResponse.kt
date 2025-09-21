package com.airsaid.localization.translate.impl.google

import com.google.gson.annotations.SerializedName

internal data class GoogleTranslationResponse(
  @SerializedName("sentences") val sentences: List<Sentence>?,
  @SerializedName("src") val sourceLanguage: String?,
  @SerializedName("ld_result") val languageDetection: LanguageDetectionResult?,
  @SerializedName("error") val error: ErrorBody? = null
) {
  data class Sentence(
    @SerializedName("trans") val translation: String?,
    @SerializedName("orig") val original: String?
  )

  data class LanguageDetectionResult(
    @SerializedName("srclangs") val sourceLanguages: List<String>?
  )

  data class ErrorBody(
    @SerializedName("message") val message: String? = null
  )
}
