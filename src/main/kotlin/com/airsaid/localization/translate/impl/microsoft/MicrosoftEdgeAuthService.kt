package com.airsaid.localization.translate.impl.microsoft

import com.airsaid.localization.translate.util.HttpRequestFactory
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.diagnostic.logger
import com.intellij.util.concurrency.annotations.RequiresBackgroundThread
import com.intellij.util.io.HttpRequests
import java.io.IOException
import java.util.*
import java.util.concurrent.atomic.AtomicReference

/**
 * Fetches and caches Microsoft Translator access tokens using the same public
 * endpoint leveraged by the Microsoft Edge browser. This removes the need for
 * user-provided subscription keys.
 */
@Service
class MicrosoftEdgeAuthService {

  private val tokenRef = AtomicReference<Token?>()

  private val gson = Gson()

  @RequiresBackgroundThread
  @Throws(MicrosoftAuthenticationException::class)
  fun getAccessToken(): String {
    val currentTime = System.currentTimeMillis()
    tokenRef.get()?.takeIf { currentTime < it.expireAtMillis }?.let { return it.value }

    synchronized(this) {
      tokenRef.get()?.takeIf { System.currentTimeMillis() < it.expireAtMillis }?.let { return it.value }
      val token = requestAccessToken()
      val expireAt = extractExpirationTime(token)
      val cached = Token(token, expireAt)
      tokenRef.set(cached)
      LOG.debug("Fetched Microsoft access token. Expires at ${Date(expireAt)}")
      return cached.value
    }
  }

  @Throws(MicrosoftAuthenticationException::class)
  private fun requestAccessToken(): String {
    val endpoint = authUrlOverride ?: AUTH_URL
    return try {
      HttpRequestFactory.get(endpoint, CONNECTION_TIMEOUT_MS)
        .tuner { connection ->
          // Endpoint expects a modern browser style user agent
          connection.setRequestProperty("Accept", "*/*")
          connection.setRequestProperty(
            "User-Agent",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 " +
                "(KHTML, like Gecko) Chrome/118.0.0.0 Safari/537.36"
          )
        }
        .connect { request -> request.readString(null) }
        .also { token ->
          if (!TOKEN_REGEX.matches(token.trim())) {
            throw MicrosoftAuthenticationException("Authentication failed: invalid token format")
          }
        }
    } catch (statusEx: HttpRequests.HttpStatusException) {
      throw MicrosoftAuthenticationException(
        "Authentication failed: HTTP ${statusEx.statusCode}",
        statusEx
      )
    } catch (io: IOException) {
      throw MicrosoftAuthenticationException("Authentication failed: ${io.message}", io)
    }
  }

  private fun extractExpirationTime(token: String): Long {
    return try {
      val payload = token.split('.')
        .getOrNull(1)
        ?.let { chunk ->
          val decoder = Base64.getUrlDecoder()
          String(decoder.decode(chunk))
        }
        ?: return System.currentTimeMillis() + DEFAULT_EXPIRATION_MS
      val jwt = gson.fromJson(payload, JwtPayload::class.java)
      jwt.expirationTime * 1_000L - PRE_EXPIRATION_BUFFER_MS
    } catch (_: Throwable) {
      System.currentTimeMillis() + DEFAULT_EXPIRATION_MS
    }
  }

  private data class Token(val value: String, val expireAtMillis: Long)

  private data class JwtPayload(@SerializedName("exp") val expirationTime: Long)

  internal fun clearCacheForTests() {
    tokenRef.set(null)
  }

  companion object {
    private val LOG: Logger = logger<MicrosoftEdgeAuthService>()

    private const val AUTH_URL = "https://edge.microsoft.com/translate/auth"
    private const val CONNECTION_TIMEOUT_MS = 15_000
    private const val PRE_EXPIRATION_BUFFER_MS = 2 * 60 * 1_000L // Refresh 2 minutes early
    private const val DEFAULT_EXPIRATION_MS = 8 * 60 * 1_000L
    private val TOKEN_REGEX = Regex("""^[A-Za-z0-9\-_]+(\.[A-Za-z0-9\-_]+){2}$""")

    @Volatile
    internal var authUrlOverride: String? = null

    fun getInstance(): MicrosoftEdgeAuthService = service()

    internal fun resetForTests() {
      getInstance().clearCacheForTests()
    }
  }
}
