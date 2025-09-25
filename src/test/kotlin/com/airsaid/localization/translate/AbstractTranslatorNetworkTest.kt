package com.airsaid.localization.translate

import com.airsaid.localization.translate.lang.Lang
import com.airsaid.localization.translate.lang.Languages
import com.airsaid.localization.translate.lang.toLang
import com.intellij.openapi.util.Pair
import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.concurrent.CountDownLatch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference

class AbstractTranslatorNetworkTest {

  private lateinit var server: HttpServer
  private lateinit var executor: ExecutorService
  private lateinit var baseUrl: String

  @BeforeEach
  fun setUp() {
    executor = Executors.newSingleThreadExecutor()
    val port = findFreePort()
    server = HttpServer.create(InetSocketAddress("127.0.0.1", port), 0)
    server.executor = executor
    baseUrl = "http://127.0.0.1:$port"
    server.start()
  }

  @AfterEach
  fun tearDown() {
    server.stop(0)
    executor.shutdownNow()
  }

  @Test
  fun `doTranslate posts form encoded payload`() {
    val capturedRequest = AtomicReference<CapturedRequest>()
    val latch = CountDownLatch(1)

    server.createContext("/translate") { exchange ->
      capturedRequest.set(exchange.captureRequest())
      exchange.respondWith(200, "\"ok\"")
      latch.countDown()
    }

    val translator = object : TestTranslator("/translate") {
      override fun getRequestParams(fromLang: Lang, toLang: Lang, text: String): List<Pair<String, String>> {
        return listOf(
          Pair.create("q", text),
          Pair.create("lang", toLang.translationCode)
        )
      }

      override fun parsingResult(fromLang: Lang, toLang: Lang, text: String, resultText: String): String {
        return resultText
      }
    }

    val inputText = "Hello world + test"
    val result = translator.doTranslate(Languages.AUTO.toLang(), Languages.ENGLISH.toLang(), inputText)

    assertTrue(latch.await(2, TimeUnit.SECONDS))
    assertEquals("\"ok\"", result)

    val request = capturedRequest.get()
    assertEquals("POST", request.method)
    assertTrue(request.contentType?.contains("application/x-www-form-urlencoded") == true)

    val expectedBody = listOf(
      "q" to inputText,
      "lang" to Languages.ENGLISH.toLang().translationCode,
    ).joinToString("&") { (name, value) ->
      "${name}=${URLEncoder.encode(value, StandardCharsets.UTF_8)}"
    }
    assertEquals(expectedBody, request.body)
  }

  @Test
  fun `doTranslate posts raw json body when content type overrides`() {
    val capturedRequest = AtomicReference<CapturedRequest>()
    val latch = CountDownLatch(1)

    server.createContext("/json") { exchange ->
      capturedRequest.set(exchange.captureRequest())
      exchange.respondWith(200, "{\"translated\":\"ok\"}")
      latch.countDown()
    }

    val translator = object : TestTranslator("/json") {
      override val requestContentType: String
        get() = "application/json"

      override fun getRequestParams(fromLang: Lang, toLang: Lang, text: String): List<Pair<String, String>> {
        return emptyList()
      }

      override fun getRequestBody(fromLang: Lang, toLang: Lang, text: String): String {
        return "{\"text\":\"$text\",\"target\":\"${toLang.translationCode}\"}"
      }

      override fun parsingResult(fromLang: Lang, toLang: Lang, text: String, resultText: String): String {
        return resultText
      }
    }

    val inputText = "Hello"
    val result = translator.doTranslate(Languages.AUTO.toLang(), Languages.ENGLISH.toLang(), inputText)

    assertTrue(latch.await(2, TimeUnit.SECONDS))
    assertEquals("{\"translated\":\"ok\"}", result)

    val request = capturedRequest.get()
    assertEquals("POST", request.method)
    assertTrue(request.contentType?.contains("application/json") == true)
    val expectedBody = "{\"text\":\"$inputText\",\"target\":\"${Languages.ENGLISH.toLang().translationCode}\"}"
    assertEquals(expectedBody, request.body)
  }

  private fun findFreePort(): Int {
    ServerSocket(0).use { socket ->
      socket.reuseAddress = true
      return socket.localPort
    }
  }

  private open inner class TestTranslator(private val endpoint: String) : AbstractTranslator() {
    override val key: String = "Test"
    override val name: String = "Test"
    override val supportedLanguages: List<Lang> = listOf(Languages.ENGLISH.toLang())

    override fun getRequestUrl(fromLang: Lang, toLang: Lang, text: String): String {
      return baseUrl + endpoint
    }

    override fun parsingResult(fromLang: Lang, toLang: Lang, text: String, resultText: String): String {
      throw UnsupportedOperationException("TestTranslator should override parsingResult")
    }
  }

  private data class CapturedRequest(
    val method: String,
    val body: String,
    val contentType: String?,
  )

  private fun HttpExchange.captureRequest(): CapturedRequest {
    val bodyText = requestBody.bufferedReader(StandardCharsets.UTF_8).use { it.readText() }
    val contentType = requestHeaders.getFirstIgnoreCase("Content-Type")
    return CapturedRequest(requestMethod, bodyText, contentType)
  }

  private fun HttpExchange.respondWith(status: Int, payload: String) {
    val bytes = payload.toByteArray(StandardCharsets.UTF_8)
    sendResponseHeaders(status, bytes.size.toLong())
    responseBody.use { out ->
      out.write(bytes)
    }
  }

  private fun com.sun.net.httpserver.Headers.getFirstIgnoreCase(name: String): String? {
    return this.entries.firstOrNull { it.key.equals(name, ignoreCase = true) }?.value?.firstOrNull()
  }
}
