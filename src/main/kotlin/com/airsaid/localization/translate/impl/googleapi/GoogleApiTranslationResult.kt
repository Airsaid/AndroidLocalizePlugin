package com.airsaid.localization.translate.impl.googleapi

import com.airsaid.localization.translate.TranslationResult

/**
 * @author airsaid
 */
data class GoogleApiTranslationResult(
    var data: Data? = null,
    var error: Error? = null
) : TranslationResult {

    fun isSuccess(): Boolean = data != null && error == null

    override val translationResult: String
        get() = data?.translations?.get(0)?.translatedText ?: ""

    data class Data(
        var translations: Array<Translation>? = null
    ) {
        data class Translation(
            var translatedText: String? = null,
            var detectedSourceLanguage: String? = null
        )

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Data

            if (translations != null) {
                if (other.translations == null) return false
                if (!translations.contentEquals(other.translations)) return false
            } else if (other.translations != null) return false

            return true
        }

        override fun hashCode(): Int {
            return translations?.contentHashCode() ?: 0
        }
    }

    data class Error(
        var code: Int = 0,
        var message: String? = null
    )
}