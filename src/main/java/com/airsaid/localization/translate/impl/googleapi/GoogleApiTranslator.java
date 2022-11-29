package com.airsaid.localization.translate.impl.googleapi;

import com.airsaid.localization.translate.AbstractTranslator;
import com.airsaid.localization.translate.TranslationException;
import com.airsaid.localization.translate.impl.google.AbsGoogleTranslator;
import com.airsaid.localization.translate.lang.Lang;
import com.airsaid.localization.translate.util.GsonUtil;
import com.google.auto.service.AutoService;
import com.intellij.openapi.util.Pair;
import com.intellij.util.io.RequestBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author airsaid
 */
@AutoService(AbstractTranslator.class)
public class GoogleApiTranslator extends AbsGoogleTranslator {
  private static final String KEY = "GoogleApi";
  private static final String HOST_URL = "https://translation.googleapis.com";
  private static final String TRANSLATE_URL = HOST_URL.concat("/language/translate/v2");
  private static final String APPLY_APP_ID_URL = "https://cloud.google.com/translate";

  @Override
  public @NotNull String getKey() {
    return KEY;
  }

  @Override
  public @NotNull String getName() {
    return "Google (API)";
  }

  @Override
  public @NotNull String getRequestUrl(@NotNull Lang fromLang, @NotNull Lang toLang, @NotNull String text) {
    return TRANSLATE_URL;
  }

  @Override
  public String getAppKeyDisplay() {
    return "API Key";
  }

  @Nullable
  @Override
  public String getApplyAppIdUrl() {
    return APPLY_APP_ID_URL;
  }

  @Override
  public boolean isNeedAppId() {
    return false;
  }

  @Override
  public @NotNull List<Pair<String, String>> getRequestParams(@NotNull Lang fromLang, @NotNull Lang toLang, @NotNull String text) {
    List<Pair<String, String>> params = new ArrayList<>();
    params.add(Pair.create("q", text));
    params.add(Pair.create("target", toLang.getTranslationCode()));
    params.add(Pair.create("key", getAppKey()));
    params.add(Pair.create("format", "text"));
    return params;
  }

  @Override
  public void configureRequestBuilder(@NotNull RequestBuilder requestBuilder) {
    requestBuilder.tuner(connection -> connection.setRequestProperty("Referer", HOST_URL));
  }

  @Override
  public @NotNull String parsingResult(@NotNull Lang fromLang, @NotNull Lang toLang, @NotNull String text, @NotNull String resultText) {
    LOG.info("parsingResult: " + resultText);
    GoogleApiTranslationResult result = GsonUtil.getInstance().getGson().fromJson(resultText, GoogleApiTranslationResult.class);
    if (result.isSuccess()) {
      return result.getTranslationResult();
    } else {
      String message;
      if (result.error != null) {
        message = result.error.message.concat("(").concat(String.valueOf(result.error.code)).concat(")");
      } else {
        message = "Unknown error";
      }
      throw new TranslationException(fromLang, toLang, text, message);
    }
  }
}
