package com.airsaid.localization.translate.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author airsaid
 */
public class UrlBuilderTest {
  @Test
  public void testNoParameterBuild() {
    String result = new UrlBuilder("https://translate.googleapis.com/translate_a/single")
        .build();
    Assertions.assertEquals("https://translate.googleapis.com/translate_a/single", result);
  }

  @Test
  public void testSingleParameterBuild() {
    String result = new UrlBuilder("https://translate.googleapis.com/translate_a/single")
        .addQueryParameter("sl", "en")
        .build();
    Assertions.assertEquals("https://translate.googleapis.com/translate_a/single?sl=en", result);
  }

  @Test
  public void testSomeParameterBuild() {
    String result = new UrlBuilder("https://translate.googleapis.com/translate_a/single")
        .addQueryParameter("sl", "en")
        .addQueryParameter("tl", "zh-CN")
        .addQueryParameter("client", "gtx")
        .addQueryParameter("dt", "t")
        .build();
    Assertions.assertEquals("https://translate.googleapis.com/translate_a/single?sl=en&tl=zh-CN&client=gtx&dt=t", result);
  }

  @Test
  public void testRepeatParameterBuild() {
    String result = new UrlBuilder("https://translate.googleapis.com/translate_a/single")
        .addQueryParameter("sl", "en")
        .addQueryParameter("tl", "zh-CN")
        .addQueryParameters("dt", "t", "bd", "ex")
        .build();
    Assertions.assertEquals("https://translate.googleapis.com/translate_a/single?sl=en&tl=zh-CN&dt=t&dt=bd&dt=ex", result);
  }
}