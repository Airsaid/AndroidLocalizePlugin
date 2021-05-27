package com.airsaid.localization.translate;

import com.airsaid.localization.translate.lang.Lang;
import com.intellij.util.io.HttpRequests;
import com.intellij.util.io.RequestBuilder;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;

/**
 * @author airsaid
 */
public abstract class AbstractTranslator implements Translator {

  private static final String CONTENT_TYPE = "application/x-www-form-urlencoded";

  @Override
  public String doTranslate(@NotNull Lang fromLang, @NotNull Lang toLang, @NotNull String text) {
    try {
      RequestBuilder requestBuilder = HttpRequests.post(getRequestUrl(fromLang, toLang, text), CONTENT_TYPE);
      configureRequestBuilder(requestBuilder);
      return requestBuilder.connect(this::parsingRequest);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return "";
  }

  @NotNull
  public abstract List<Lang> getSupportedLanguages();

  @NotNull
  public abstract String getRequestUrl(@NotNull Lang fromLang, @NotNull Lang toLang, @NotNull String text);

  public abstract void configureRequestBuilder(@NotNull RequestBuilder requestBuilder);

  @NotNull
  public abstract String parsingRequest(HttpRequests.Request request);

}
