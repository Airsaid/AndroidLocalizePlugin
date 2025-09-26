package com.airsaid.localization.translate.services

import com.airsaid.localization.translate.AbstractTranslator
import com.airsaid.localization.translate.lang.Lang
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

/**
 * Unit tests covering translator selection logic.
 *
 * @author airsaid
 */
class TranslatorServiceTest {

  @Test
  fun `selectDefaultTranslator returns first translator`() {
    val first = StubTranslator("First")
    val second = StubTranslator("Second")
    val translators = linkedMapOf(
      first.key to first,
      second.key to second,
    )

    val defaultTranslator = TranslatorService.selectDefaultTranslator(translators)

    assertEquals(first, defaultTranslator)
  }

  /**
   * Minimal translator stub used to drive selection scenarios.
   *
   * @author airsaid
   */
  private class StubTranslator(override val key: String) : AbstractTranslator() {
    override val name: String = key
    override val supportedLanguages: List<Lang> = emptyList()

    override fun getRequestUrl(fromLang: Lang, toLang: Lang, text: String): String {
      throw UnsupportedOperationException()
    }

    override fun parsingResult(fromLang: Lang, toLang: Lang, text: String, resultText: String): String {
      throw UnsupportedOperationException()
    }
  }
}
