package com.airsaid.localization.translate.impl.google;

import com.airsaid.localization.translate.AbstractTranslator;
import com.airsaid.localization.translate.lang.Lang;
import com.airsaid.localization.translate.util.AgentUtil;
import com.airsaid.localization.translate.util.UrlBuilder;
import com.intellij.util.io.HttpRequests;
import com.intellij.util.io.RequestBuilder;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * @author airsaid
 */
public class GoogleTranslator extends AbstractTranslator {

  public static final String HOST_URL = "https://translate.google.cn";
  public static final String BASE_URL = HOST_URL.concat("/translate_a/single");

  @Override
  @NotNull
  public List<Lang> getSupportedLanguages() {
    return Collections.emptyList();
  }

  @Override
  public @NotNull String getRequestUrl(@NotNull Lang fromLang, @NotNull Lang toLang, @NotNull String text) {
    return new UrlBuilder(BASE_URL)
        .addQueryParameter("sl", fromLang.getCode()) // source language code (auto for auto detection)
        .addQueryParameter("tl", toLang.getCode()) // translation language
        .addQueryParameter("client", "gtx") // client of request (guess)
        .addQueryParameters("dt", "t", "bd", "rm", "qca") // specify what to return
        .addQueryParameter("dj", "1") // json response with names
        .addQueryParameter("ie", "UTF-8") // input encoding
        .addQueryParameter("oe", "UTF-8") // output encoding
        .addQueryParameter("tk", GoogleToken.getToken(text)) // translate token
        .build();
  }

  @Override
  public void configureRequestBuilder(@NotNull RequestBuilder requestBuilder) {
    requestBuilder.userAgent(AgentUtil.getUserAgent());
  }

  @Override
  public @NotNull String parsingRequest(HttpRequests.Request request) {
    try {
      return request.readString();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return "";
  }
}
