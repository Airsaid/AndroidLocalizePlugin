package com.airsaid.localization.translate.impl.google;

import com.airsaid.localization.translate.AbstractTranslator;
import com.airsaid.localization.translate.lang.Lang;
import com.airsaid.localization.translate.util.AgentUtil;
import com.airsaid.localization.translate.util.GsonUtil;
import com.airsaid.localization.translate.util.UrlBuilder;
import com.intellij.openapi.util.Pair;
import com.intellij.util.io.RequestBuilder;
import org.apache.commons.lang.StringEscapeUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
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
    Lang[] values = Lang.values();
    List<Lang> result = new ArrayList<>(values.length - 1);
    for (Lang lang : values) {
      if (lang != Lang.AUTO) {
        result.add(lang);
      }
    }
    return result;
  }

  @Override
  public @NotNull String getRequestUrl(@NotNull Lang fromLang, @NotNull Lang toLang, @NotNull String text) {
    return new UrlBuilder(BASE_URL)
        .addQueryParameter("sl", fromLang.getCode()) // source language code (auto for auto detection)
        .addQueryParameter("tl", toLang.getCode()) // translation language
        .addQueryParameter("client", "gtx") // client of request (guess)
        .addQueryParameters("dt", "t") // specify what to return
        .addQueryParameter("dj", "1") // json response with names
        .addQueryParameter("ie", "UTF-8") // input encoding
        .addQueryParameter("oe", "UTF-8") // output encoding
        .addQueryParameter("tk", GoogleToken.getToken(text)) // translate token
        .build();
  }

  @Override
  public @NotNull List<Pair<String, String>> getRequestParams(@NotNull Lang fromLang, @NotNull Lang toLang, @NotNull String text) {
    List<Pair<String, String>> params = new ArrayList<>();
    params.add(Pair.create("q", StringEscapeUtils.escapeJava(text)));
    return params;
  }

  @Override
  public void configureRequestBuilder(@NotNull RequestBuilder requestBuilder) {
    requestBuilder.userAgent(AgentUtil.getUserAgent())
        .tuner(connection -> connection.setRequestProperty("Referer", GoogleTranslator.HOST_URL));
  }

  @Override
  public @NotNull String parsingResult(@NotNull Lang fromLang, @NotNull Lang toLang, @NotNull String text, @NotNull String resultText) {
    GoogleTranslationResult googleTranslationResult = GsonUtil.getInstance().getGson().fromJson(resultText, GoogleTranslationResult.class);
    return googleTranslationResult.getTranslationResult();
  }
}
