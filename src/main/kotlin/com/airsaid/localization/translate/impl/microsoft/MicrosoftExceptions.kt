package com.airsaid.localization.translate.impl.microsoft

import java.io.IOException

class MicrosoftAuthenticationException(
  message: String? = null,
  cause: Throwable? = null
) : IOException(message, cause)
