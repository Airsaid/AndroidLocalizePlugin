/*
 * Copyright 2021 Airsaid. https://github.com/airsaid
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.airsaid.localization.translate;

import com.airsaid.localization.config.SettingsState;
import com.airsaid.localization.translate.lang.Lang;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.Pair;
import com.intellij.util.io.HttpRequests;
import com.intellij.util.io.RequestBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author airsaid
 */
public abstract class AbstractTranslator implements Translator, TranslatorConfigurable {

  protected static final Logger LOG = Logger.getInstance(AbstractTranslator.class);

  private static final String CONTENT_TYPE = "application/x-www-form-urlencoded";

  @Override
  public String doTranslate(@NotNull Lang fromLang, @NotNull Lang toLang, @NotNull String text) throws TranslationException {
    checkSupportedLanguages(fromLang, toLang, text);

    String requestUrl = getRequestUrl(fromLang, toLang, text);
    RequestBuilder requestBuilder = HttpRequests.post(requestUrl, CONTENT_TYPE);
    // Set the timeout time to 60 seconds.
    requestBuilder.connectTimeout(60 * 1000);
    configureRequestBuilder(requestBuilder);

    try {
      return requestBuilder.connect(request -> {
        String requestParams = getRequestParams(fromLang, toLang, text)
            .stream()
            .map(pair -> {
              return pair.first.concat("=").concat(URLEncoder.encode(pair.second, StandardCharsets.UTF_8));
            })
            .collect(Collectors.joining("&"));
        if (!requestParams.isEmpty()) {
          request.write(requestParams);
        }
        String requestBody = getRequestBody(fromLang, toLang, text);
        if (!requestBody.isEmpty()) {
          request.write(requestBody);
        }

        String resultText = request.readString();
        return parsingResult(fromLang, toLang, text, resultText);
      });
    } catch (Exception e) {
      e.printStackTrace();
      LOG.error(e.getMessage(), e);
      throw new TranslationException(fromLang, toLang, text, e);
    }
  }

  @Override
  public @Nullable Icon getIcon() {
    return null;
  }

  @Override
  public boolean isNeedAppId() {
    return true;
  }

  @Override
  public @Nullable String getAppId() {
    return SettingsState.getInstance().getAppId(getKey());
  }

  @Override
  public String getAppIdDisplay() {
    return "APP ID";
  }

  @Override
  public boolean isNeedAppKey() {
    return true;
  }

  @Override
  public @Nullable String getAppKey() {
    return SettingsState.getInstance().getAppKey(getKey());
  }

  @Override
  public String getAppKeyDisplay() {
    return "APP KEY";
  }

  @Override
  public @Nullable String getApplyAppIdUrl() {
    return null;
  }

  @NotNull
  public String getRequestUrl(@NotNull Lang fromLang, @NotNull Lang toLang, @NotNull String text) {
    throw new UnsupportedOperationException();
  }

  @NotNull
  public List<Pair<String, String>> getRequestParams(@NotNull Lang fromLang, @NotNull Lang toLang, @NotNull String text) {
    return List.of();
  }

  @NotNull
  public String getRequestBody(@NotNull Lang fromLang, @NotNull Lang toLang, @NotNull String text) {
    return "";
  }

  public void configureRequestBuilder(@NotNull RequestBuilder requestBuilder) {

  }

  @NotNull
  public String parsingResult(@NotNull Lang fromLang, @NotNull Lang toLang, @NotNull String text, @NotNull String resultText) {
    throw new UnsupportedOperationException();
  }

  protected void checkSupportedLanguages(Lang fromLang, Lang toLang, String text) {
    List<Lang> supportedLanguages = getSupportedLanguages();
    if (!supportedLanguages.contains(toLang)) {
      throw new TranslationException(fromLang, toLang, text, toLang.getEnglishName() + " is not supported.");
    }
  }
}
