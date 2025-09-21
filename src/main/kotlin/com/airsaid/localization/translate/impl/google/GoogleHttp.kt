package com.airsaid.localization.translate.impl.google

import com.intellij.util.io.RequestBuilder

private const val GOOGLE_REFERER = "https://translate.google.com/"

internal fun googleApiUrl(path: String): String {
  val settings = GoogleTranslatorSettings.getInstance()
  val base = if (settings.useCustomServer) settings.serverUrl else GoogleTranslatorSettings.DEFAULT_SERVER_URL
  val normalizedBase = base.trim().removeSuffix("/")
  val normalizedPath = if (path.startsWith('/')) path.drop(1) else path
  return "$normalizedBase/$normalizedPath"
}

internal fun RequestBuilder.withGoogleHeaders(): RequestBuilder = apply {
  tuner { connection ->
    connection.setRequestProperty("Referer", GOOGLE_REFERER)
    connection.setRequestProperty(
      "User-Agent",
      "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 " +
        "(KHTML, like Gecko) Chrome/118.0.0.0 Safari/537.36"
    )
  }
}
