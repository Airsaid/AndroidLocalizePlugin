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

package com.airsaid.localization.translate.impl.deepl;

import com.airsaid.localization.translate.AbstractTranslator;
import com.airsaid.localization.translate.lang.Lang;
import com.airsaid.localization.translate.lang.Languages;
import com.airsaid.localization.translate.util.GsonUtil;
import com.airsaid.localization.translate.util.UrlBuilder;
import com.google.auto.service.AutoService;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.Pair;
import com.intellij.util.io.RequestBuilder;
import icons.PluginIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author musagil
 */
@AutoService(AbstractTranslator.class)
public class DeepLTranslator extends AbstractTranslator {

  private static final Logger LOG = Logger.getInstance(DeepLTranslator.class);

  private static final String KEY = "DeepL";
  private static final String HOST_URL = "https://api-free.deepl.com/v2";
  private static final String TRANSLATE_URL = HOST_URL.concat("/translate");
  private static final String APPLY_APP_ID_URL = "https://www.deepl.com/pro-api?cta=header-pro-api/";

  private List<Lang> supportedLanguages;

  @Override
  public @NotNull String getKey() {
    return KEY;
  }

  @Override
  public @NotNull String getName() {
    return "DeepL";
  }

  @Override
  public @Nullable Icon getIcon() {
    return PluginIcons.DEEP_L_ICON;
  }

  @Override
  public boolean isNeedAppId() {
    return false;
  }

  @Override
  public @NotNull List<Lang> getSupportedLanguages() {
    if (supportedLanguages == null) {
      supportedLanguages = new ArrayList<>();
      supportedLanguages.add(Languages.BULGARIAN);
      supportedLanguages.add(Languages.CZECH);
      supportedLanguages.add(Languages.DANISH);
      supportedLanguages.add(Languages.GERMAN);
      supportedLanguages.add(Languages.GREEK);
      supportedLanguages.add(new Lang(118, "en-gb", "English (British)", "English (British)"));
      supportedLanguages.add(new Lang(119, "en-us", "English (American)", "English (American)"));
      supportedLanguages.add(Languages.SPANISH);
      supportedLanguages.add(Languages.ESTONIAN);
      supportedLanguages.add(Languages.FINNISH);
      supportedLanguages.add(Languages.FRENCH);
      supportedLanguages.add(Languages.HUNGARIAN);
      supportedLanguages.add(new Lang(98, "id", "Indonesia", "Indonesian"));
      supportedLanguages.add(Languages.ITALIAN);
      supportedLanguages.add(Languages.JAPANESE);
      supportedLanguages.add(Languages.LITHUANIAN);
      supportedLanguages.add(Languages.LATVIAN);
      supportedLanguages.add(Languages.DUTCH);
      supportedLanguages.add(Languages.POLISH);
      supportedLanguages.add(new Lang(120, "pt-br", "Portuguese (Brazilian)", "Portuguese (Brazilian)"));
      supportedLanguages.add(new Lang(121, "pt-pt", "Portuguese (European)", "Portuguese (European)"));
      supportedLanguages.add(Languages.ROMANIAN);
      supportedLanguages.add(Languages.RUSSIAN);
      supportedLanguages.add(Languages.SLOVAK);
      supportedLanguages.add(Languages.SLOVENIAN);
      supportedLanguages.add(Languages.SWEDISH);
      supportedLanguages.add(Languages.TURKISH);
      supportedLanguages.add(Languages.UKRAINIAN);
      supportedLanguages.add(new Lang(104, "zh", "简体中文", "Chinese Simplified"));
    }
    return supportedLanguages;
  }

  @Override
  public String getAppKeyDisplay() {
    return "KEY";
  }

  @Override
  public @Nullable String getApplyAppIdUrl() {
    return APPLY_APP_ID_URL;
  }

  @Override
  public @NotNull String getRequestUrl(@NotNull Lang fromLang, @NotNull Lang toLang, @NotNull String text) {
    return new UrlBuilder(TRANSLATE_URL).build();
  }

  @Override
  public @NotNull List<Pair<String, String>> getRequestParams(@NotNull Lang fromLang, @NotNull Lang toLang, @NotNull String text) {
    List<Pair<String, String>> params = new ArrayList<>();
    params.add(Pair.create("text", text));
    params.add(Pair.create("target_lang", toLang.getCode()));
    return params;
  }

  @Override
  public void configureRequestBuilder(@NotNull RequestBuilder requestBuilder) {
    requestBuilder.tuner(connection -> {
      connection.setRequestProperty("Authorization", "DeepL-Auth-Key " + getAppKey());
      connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
    });
  }

  @Override
  public @NotNull String parsingResult(@NotNull Lang fromLang, @NotNull Lang toLang, @NotNull String text, @NotNull String resultText) {
    LOG.info("parsingResult: " + resultText);
    return GsonUtil.getInstance().getGson().fromJson(resultText, DeepLTranslationResult.class).getTranslationResult();
  }

}
