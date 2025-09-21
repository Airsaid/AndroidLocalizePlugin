package com.airsaid.localization.translate.services

import com.airsaid.localization.translate.AbstractTranslator
import com.airsaid.localization.translate.lang.Lang
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class TranslatorServiceTest {

  @Test
  fun `selectDefaultTranslator prefers google when present`() {
    val google = StubTranslator("Google")
    val other = StubTranslator("Other")
    val translators = linkedMapOf(
      google.key to google,
      other.key to other,
    )

    val defaultTranslator = TranslatorService.selectDefaultTranslator(translators)

    assertEquals(google, defaultTranslator)
  }

  @Test
  fun `selectDefaultTranslator falls back to first translator when google missing`() {
    val first = StubTranslator("First")
    val second = StubTranslator("Second")
    val translators = linkedMapOf(
      first.key to first,
      second.key to second,
    )

    val defaultTranslator = TranslatorService.selectDefaultTranslator(translators)

    assertEquals(first, defaultTranslator)
  }

  private class StubTranslator(override val key: String) : AbstractTranslator() {
    override val name: String = key
    override val supportedLanguages: List<Lang> = emptyList()
    override val isNeedAppId: Boolean = false
    override val isNeedAppKey: Boolean = false
    override val appId: String? get() = null
    override val appKey: String? get() = null

    override fun getRequestUrl(fromLang: Lang, toLang: Lang, text: String): String {
      throw UnsupportedOperationException()
    }

    override fun parsingResult(fromLang: Lang, toLang: Lang, text: String, resultText: String): String {
      throw UnsupportedOperationException()
    }
  }
}
