package com.airsaid.localization.translate.impl.google

import com.airsaid.localization.translate.util.HttpRequestFactory
import com.intellij.openapi.diagnostic.Logger
import com.intellij.util.concurrency.annotations.RequiresBackgroundThread
import java.lang.StrictMath.abs
import java.util.concurrent.ThreadLocalRandom
import java.util.regex.Pattern

private const val TOKEN_TTL_MILLIS = 60 * 60 * 1000L
private const val ELEMENT_JS_PATH = "/translate_a/element.js"

private val TKK_PATTERN: Pattern = Pattern.compile("tkk='(\\d+).(-?\\d+)'")

private data class Token(val value1: Long, val value2: Long, val hour: Long)

private val LOG: Logger = Logger.getInstance("GoogleToken")

private var cachedToken: Token? = null
private val tokenLock = Any()

/**
 * Clears any cached TKK tokens so subsequent calls fetch fresh credentials.
 *
 * @author airsaid
 */
internal fun resetGoogleTokenCache() {
  synchronized(tokenLock) {
    cachedToken = null
  }
}

/**
 * Returns the current TKK token pair, fetching or regenerating when required.
 *
 * @author airsaid
 */
@RequiresBackgroundThread
internal fun currentTkk(): Pair<Long, Long> {
  synchronized(tokenLock) {
    val nowHour = System.currentTimeMillis() / TOKEN_TTL_MILLIS
    cachedToken?.takeIf { it.hour == nowHour }?.let { return it.value1 to it.value2 }

    val updated = fetchFromGoogle()?.takeIf { it.hour == nowHour } ?: generateLocal(nowHour)
    cachedToken = updated
    return updated.value1 to updated.value2
  }
}

private fun fetchFromGoogle(): Token? {
  return try {
    val url = googleApiUrl(ELEMENT_JS_PATH)
    val response = HttpRequestFactory.get(url, 10_000)
      .withGoogleHeaders()
      .connect { it.readString(null) }
    val matcher = TKK_PATTERN.matcher(response)
    if (!matcher.find()) {
      LOG.warn("TKK not found in element.js response")
      null
    } else {
      val nowHour = System.currentTimeMillis() / TOKEN_TTL_MILLIS
      val value1 = matcher.group(1).toLong()
      val value2 = matcher.group(2).toLong()
      Token(value1, value2, nowHour)
    }
  } catch (error: Throwable) {
    LOG.warn("Failed to refresh Google TKK", error)
    null
  }
}

private fun generateLocal(hour: Long): Token {
  val random = ThreadLocalRandom.current()
  val value = abs(random.nextInt().toLong()) + random.nextInt().toLong()
  return Token(hour, value, hour)
}

/**
 * Computes the Google translate token for the given payload.
 *
 * @author airsaid
 */
internal fun String.tk(): String {
  val (d, e) = currentTkk()
  val bytes = mutableListOf<Long>()
  var index = 0
  while (index < length) {
    var charCode = this[index].code
    when {
      charCode < 0x80 -> bytes += charCode.toLong()
      charCode < 0x800 -> {
        bytes += (charCode shr 6 or 0xC0).toLong()
        bytes += (charCode and 0x3F or 0x80).toLong()
      }

      charCode in 0xD800..0xDBFF && index + 1 < length -> {
        val next = this[index + 1].code
        if (next and 0xFC00 == 0xDC00) {
          charCode = 0x10000 + ((charCode and 0x3FF) shl 10) + (next and 0x3FF)
          bytes += (charCode shr 18 or 0xF0).toLong()
          bytes += (charCode shr 12 and 0x3F or 0x80).toLong()
          bytes += (charCode shr 6 and 0x3F or 0x80).toLong()
          bytes += (charCode and 0x3F or 0x80).toLong()
          index++
        } else {
          bytes += (charCode shr 12 or 0xE0).toLong()
          bytes += (charCode shr 6 and 0x3F or 0x80).toLong()
          bytes += (charCode and 0x3F or 0x80).toLong()
        }
      }

      else -> {
        bytes += (charCode shr 12 or 0xE0).toLong()
        bytes += (charCode shr 6 and 0x3F or 0x80).toLong()
        bytes += (charCode and 0x3F or 0x80).toLong()
      }
    }
    index++
  }

  var result = d
  for (byte in bytes) {
    result += byte
    result = applyTransformation(result, "+-a^+6")
  }
  result = applyTransformation(result, "+-3^+b+-f")
  result = result xor e
  if (result < 0) {
    result = (result and 0x7FFFFFFF) + 0x80000000 + 1
  }
  result %= 1_000_000

  return "$result.${result xor d}"
}

private fun applyTransformation(value: Long, op: String): Long {
  var acc = value
  var i = 0
  while (i < op.length - 2) {
    val shiftChar = op[i + 2]
    val shift = if (shiftChar >= 'a') shiftChar.code - 87 else shiftChar.digitToInt()
    val operand = if (op[i + 1] == '+') acc ushr shift else acc shl shift
    acc = if (op[i] == '+') (acc + operand) and 0xFFFFFFFFL else acc xor operand
    i += 3
  }
  return acc
}
