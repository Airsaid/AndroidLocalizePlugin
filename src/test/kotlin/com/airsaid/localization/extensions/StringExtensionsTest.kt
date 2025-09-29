package com.airsaid.localization.extensions

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class StringExtensionsTest {

  @Test
  fun hasNoTranslatableText() {
    // Numbers only - should return true
    assertTrue("123".hasNoTranslatableText())
    assertTrue("0".hasNoTranslatableText())
    assertTrue("999999".hasNoTranslatableText())

    // Numbers with symbols/punctuation - should return true
    assertTrue("12.3".hasNoTranslatableText())
    assertTrue("-123".hasNoTranslatableText())
    assertTrue("+123".hasNoTranslatableText())
    assertTrue("123!@#".hasNoTranslatableText())
    assertTrue("12,34".hasNoTranslatableText())
    assertTrue("$100".hasNoTranslatableText())
    assertTrue("50%".hasNoTranslatableText())
    assertTrue("(123)".hasNoTranslatableText())

    // Numbers with whitespace - should return true
    assertTrue("12 3".hasNoTranslatableText())
    assertTrue("12\n3".hasNoTranslatableText())
    assertTrue("12\t3".hasNoTranslatableText())
    assertTrue(" 123 ".hasNoTranslatableText())

    // Symbols/punctuation only - should return true
    assertTrue("!@#".hasNoTranslatableText())
    assertTrue("...".hasNoTranslatableText())
    assertTrue("***".hasNoTranslatableText())
    assertTrue("???".hasNoTranslatableText())

    // Text with letters - should return false (needs translation)
    assertFalse("abc".hasNoTranslatableText())
    assertFalse("123abc".hasNoTranslatableText())
    assertFalse("abc123".hasNoTranslatableText())
    assertFalse("Hello".hasNoTranslatableText())
    assertFalse("Hello123".hasNoTranslatableText())
    assertFalse("123Hello".hasNoTranslatableText())
    assertFalse("中文".hasNoTranslatableText())
    assertFalse("测试123".hasNoTranslatableText())

    // Empty/null - should return true
    assertTrue("".hasNoTranslatableText())
    val nullString: String? = null
    assertTrue(nullString.hasNoTranslatableText())
  }
}