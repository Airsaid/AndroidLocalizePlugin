package com.airsaid.localization.translate.impl.google;

import com.airsaid.localization.translate.AbstractTranslator;
import com.airsaid.localization.translate.lang.Lang;
import com.airsaid.localization.translate.lang.Languages;
import com.airsaid.localization.translate.util.AgentUtil;
import com.airsaid.localization.translate.util.GsonUtil;
import com.airsaid.localization.translate.util.UrlBuilder;
import com.intellij.openapi.util.Pair;
import com.intellij.util.io.RequestBuilder;
import icons.PluginIcons;
import org.apache.commons.lang.StringEscapeUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author airsaid
 */
public class GoogleTranslator extends AbstractTranslator {
  private static final String KEY = "Google";

  private static final String HOST_URL_CN = "https://translate.google.cn";
  private static final String HOST_URL_COM = "https://translate.google.com";
  public static String HOST_URL = HOST_URL_CN;
  private static String BASE_URL = HOST_URL.concat("/translate_a/single");

  private List<Lang> supportedLanguages;

  public static void setUseComHost(boolean useComHost) {
    HOST_URL = useComHost ? HOST_URL_COM : HOST_URL_CN;
    BASE_URL = HOST_URL.concat("/translate_a/single");
  }

  @Override
  public @NotNull String getKey() {
    return KEY;
  }

  @Override
  public @NotNull String getName() {
    return "Google";
  }

  @Override
  public @NotNull Icon getIcon() {
    return PluginIcons.GOOGLE_ICON;
  }

  @Override
  public boolean isNeedAppId() {
    return false;
  }

  @Override
  public boolean isNeedAppKey() {
    return false;
  }

  @Override
  @NotNull
  public List<Lang> getSupportedLanguages() {
    if (supportedLanguages == null) {
      List<Lang> languages = Languages.getLanguages();
      supportedLanguages = new ArrayList<>(104);
      for (int i = 1; i <= 104; i++) {
        supportedLanguages.add(languages.get(i));
      }
    }
    return supportedLanguages;
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
    LOG.info("parsingResult: " + resultText);
    GoogleTranslationResult googleTranslationResult = GsonUtil.getInstance().getGson().fromJson(resultText, GoogleTranslationResult.class);
    return googleTranslationResult.getTranslationResult();
  }
}
