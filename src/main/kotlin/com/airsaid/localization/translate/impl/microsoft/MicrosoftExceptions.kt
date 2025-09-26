package com.airsaid.localization.translate.impl.microsoft

import java.io.IOException

/**
 * Exception thrown when Microsoft authentication fails.
 *
 * @author airsaid
 */
class MicrosoftAuthenticationException(
  message: String? = null,
  cause: Throwable? = null
) : IOException(message, cause)
