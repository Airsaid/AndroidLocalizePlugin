package com.airsaid.localization.translate.util

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

/**
 * @author airsaid
 */
class UrlBuilderTest {

  @Test
  fun testNoParameterBuild() {
    val result = UrlBuilder("https://translate.googleapis.com/translate_a/single")
      .build()
    Assertions.assertEquals("https://translate.googleapis.com/translate_a/single", result)
  }

  @Test
  fun testSingleParameterBuild() {
    val result = UrlBuilder("https://translate.googleapis.com/translate_a/single")
      .addQueryParameter("sl", "en")
      .build()
    Assertions.assertEquals("https://translate.googleapis.com/translate_a/single?sl=en", result)
  }

  @Test
  fun testSomeParameterBuild() {
    val result = UrlBuilder("https://translate.googleapis.com/translate_a/single")
      .addQueryParameter("sl", "en")
      .addQueryParameter("tl", "zh-CN")
      .addQueryParameter("client", "gtx")
      .addQueryParameter("dt", "t")
      .build()
    Assertions.assertEquals(
      "https://translate.googleapis.com/translate_a/single?sl=en&tl=zh-CN&client=gtx&dt=t",
      result
    )
  }

  @Test
  fun testRepeatParameterBuild() {
    val result = UrlBuilder("https://translate.googleapis.com/translate_a/single")
      .addQueryParameter("sl", "en")
      .addQueryParameter("tl", "zh-CN")
      .addQueryParameters("dt", "t", "bd", "ex")
      .build()
    Assertions.assertEquals(
      "https://translate.googleapis.com/translate_a/single?sl=en&tl=zh-CN&dt=t&dt=bd&dt=ex",
      result
    )
  }
}