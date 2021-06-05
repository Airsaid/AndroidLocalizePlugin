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
import java.io.IOException;
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
    List<Lang> supportedLanguages = getSupportedLanguages();
    if (!supportedLanguages.contains(toLang)) {
      throw new TranslationException(fromLang, toLang, text, toLang.getEnglishName() + " is not supported.");
    }

    String requestUrl = getRequestUrl(fromLang, toLang, text);
    RequestBuilder requestBuilder = HttpRequests.post(requestUrl, CONTENT_TYPE);
    configureRequestBuilder(requestBuilder);

    try {
      return requestBuilder.connect(request -> {
        String requestParams = getRequestParams(fromLang, toLang, text)
            .stream()
            .map(pair -> pair.first.concat("=").concat(pair.second))
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
    } catch (IOException e) {
      e.printStackTrace();
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
  public boolean isNeedAppKey() {
    return true;
  }

  @Override
  public @Nullable String getAppKey() {
    return SettingsState.getInstance().getAppKey(getKey());
  }

  @Override
  public @Nullable String getApplyAppIdUrl() {
    return null;
  }

  @NotNull
  public abstract String getRequestUrl(@NotNull Lang fromLang, @NotNull Lang toLang, @NotNull String text);

  @NotNull
  public abstract List<Pair<String, String>> getRequestParams(@NotNull Lang fromLang, @NotNull Lang toLang, @NotNull String text);

  @NotNull
  public String getRequestBody(@NotNull Lang fromLang, @NotNull Lang toLang, @NotNull String text) {
    return "";
  }

  public abstract void configureRequestBuilder(@NotNull RequestBuilder requestBuilder);

  @NotNull
  public abstract String parsingResult(@NotNull Lang fromLang, @NotNull Lang toLang, @NotNull String text, @NotNull String resultText);
}
