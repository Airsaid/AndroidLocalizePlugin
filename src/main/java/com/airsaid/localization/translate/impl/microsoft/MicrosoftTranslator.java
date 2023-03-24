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

package com.airsaid.localization.translate.impl.microsoft;

import com.airsaid.localization.translate.AbstractTranslator;
import com.airsaid.localization.translate.lang.Lang;
import com.airsaid.localization.translate.lang.Languages;
import com.airsaid.localization.translate.util.GsonUtil;
import com.airsaid.localization.translate.util.UrlBuilder;
import com.google.auto.service.AutoService;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.util.io.RequestBuilder;
import icons.PluginIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author airsaid
 */
@AutoService(AbstractTranslator.class)
public class MicrosoftTranslator extends AbstractTranslator {

  private static final Logger LOG = Logger.getInstance(MicrosoftTranslator.class);

  private static final String KEY = "Microsoft";
  private static final String HOST_URL = "https://api.cognitive.microsofttranslator.com";
  private static final String TRANSLATE_URL = HOST_URL.concat("/translate");
  private static final String APPLY_APP_ID_URL = "https://docs.microsoft.com/azure/cognitive-services/translator/translator-how-to-signup";

  private List<Lang> supportedLanguages;

  @Override
  public @NotNull String getKey() {
    return KEY;
  }

  @Override
  public @NotNull String getName() {
    return "Microsoft";
  }

  @Override
  public @Nullable Icon getIcon() {
    return PluginIcons.MICROSOFT_ICON;
  }

  @Override
  public boolean isNeedAppId() {
    return false;
  }

  @Override
  public @NotNull List<Lang> getSupportedLanguages() {
    if (supportedLanguages == null) {
      supportedLanguages = new ArrayList<>();
      supportedLanguages.add(Languages.AFRIKAANS);
      supportedLanguages.add(Languages.ALBANIAN);
      supportedLanguages.add(Languages.AMHARIC);
      supportedLanguages.add(Languages.ARABIC);
      supportedLanguages.add(Languages.ARMENIAN);
      supportedLanguages.add(Languages.ASSAMESE);
      supportedLanguages.add(Languages.AZERBAIJANI);
      supportedLanguages.add(Languages.BANGLA);
      supportedLanguages.add(Languages.BOSNIAN);
      supportedLanguages.add(Languages.BULGARIAN);
      supportedLanguages.add(Languages.CATALAN);
      supportedLanguages.add(Languages.CHINESE_SIMPLIFIED.setTranslationCode("zh-Hans"));
      supportedLanguages.add(Languages.CHINESE_TRADITIONAL.setTranslationCode("zh-Hant"));
      supportedLanguages.add(Languages.CROATIAN);
      supportedLanguages.add(Languages.CZECH);
      supportedLanguages.add(Languages.DANISH);
      supportedLanguages.add(Languages.DARI);
      supportedLanguages.add(Languages.DUTCH);
      supportedLanguages.add(Languages.ENGLISH);
      supportedLanguages.add(Languages.ESTONIAN);
      supportedLanguages.add(Languages.FIJIAN);
      supportedLanguages.add(Languages.FILIPINO.setTranslationCode("fil"));
      supportedLanguages.add(Languages.FINNISH);
      supportedLanguages.add(Languages.FRENCH);
      supportedLanguages.add(Languages.GERMAN);
      supportedLanguages.add(Languages.GREEK);
      supportedLanguages.add(Languages.GUJARATI);
      supportedLanguages.add(Languages.HAITIAN_CREOLE);
      supportedLanguages.add(Languages.HEBREW.setTranslationCode("he"));
      supportedLanguages.add(Languages.HINDI);
      supportedLanguages.add(Languages.HMONG_DAW);
      supportedLanguages.add(Languages.HUNGARIAN);
      supportedLanguages.add(Languages.ICELANDIC);
      supportedLanguages.add(Languages.INDONESIAN.setTranslationCode("id"));
      supportedLanguages.add(Languages.INUKTITUT);
      supportedLanguages.add(Languages.IRISH);
      supportedLanguages.add(Languages.ITALIAN);
      supportedLanguages.add(Languages.JAPANESE);
      supportedLanguages.add(Languages.KANNADA);
      supportedLanguages.add(Languages.KAZAKH);
      supportedLanguages.add(Languages.KHMER);
      supportedLanguages.add(Languages.KLINGON_LATIN);
      supportedLanguages.add(Languages.KLINGON_PIQAD);
      supportedLanguages.add(Languages.KOREAN);
      supportedLanguages.add(Languages.KURDISH);
      supportedLanguages.add(Languages.LAO);
      supportedLanguages.add(Languages.LATVIAN);
      supportedLanguages.add(Languages.LITHUANIAN);
      supportedLanguages.add(Languages.MALAGASY);
      supportedLanguages.add(Languages.MALAY);
      supportedLanguages.add(Languages.MALAYALAM);
      supportedLanguages.add(Languages.MALTESE);
      supportedLanguages.add(Languages.MAORI);
      supportedLanguages.add(Languages.MARATHI);
      supportedLanguages.add(Languages.BURMESE);
      supportedLanguages.add(Languages.NEPALI);
      supportedLanguages.add(Languages.NORWEGIAN.setTranslationCode("nb"));
      supportedLanguages.add(Languages.ODIA);
      supportedLanguages.add(Languages.PASHTO);
      supportedLanguages.add(Languages.PERSIAN);
      supportedLanguages.add(Languages.PORTUGUESE);
      supportedLanguages.add(Languages.PUNJABI);
      supportedLanguages.add(Languages.QUERETARO_OTOMI);
      supportedLanguages.add(Languages.ROMANIAN);
      supportedLanguages.add(Languages.RUSSIAN);
      supportedLanguages.add(Languages.SAMOAN);
      supportedLanguages.add(Languages.SERBIAN);
      supportedLanguages.add(Languages.SLOVAK);
      supportedLanguages.add(Languages.SLOVENIAN);
      supportedLanguages.add(Languages.SPANISH);
      supportedLanguages.add(Languages.SWAHILI);
      supportedLanguages.add(Languages.SWEDISH);
      supportedLanguages.add(Languages.TAHITIAN);
      supportedLanguages.add(Languages.TAMIL);
      supportedLanguages.add(Languages.TELUGU);
      supportedLanguages.add(Languages.THAI);
      supportedLanguages.add(Languages.TIGRINYA);
      supportedLanguages.add(Languages.TONGAN);
      supportedLanguages.add(Languages.TURKISH);
      supportedLanguages.add(Languages.UKRAINIAN);
      supportedLanguages.add(Languages.URDU);
      supportedLanguages.add(Languages.VIETNAMESE);
      supportedLanguages.add(Languages.WELSH);
      supportedLanguages.add(Languages.YUCATEC_MAYA);
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
    return new UrlBuilder(TRANSLATE_URL)
        .addQueryParameter("api-version", "3.0")
        .addQueryParameter("to", toLang.getTranslationCode())
        .build();
  }

  @Override
  @NotNull
  public String getRequestBody(@NotNull Lang fromLang, @NotNull Lang toLang, @NotNull String text) {
    return "[{\"Text\": \"" + text + "\"}]";
  }

  @Override
  public void configureRequestBuilder(@NotNull RequestBuilder requestBuilder) {
    requestBuilder.tuner(connection -> {
      connection.setRequestProperty("Ocp-Apim-Subscription-Key", getAppKey());
      connection.setRequestProperty("Content-type", "application/json");
    });
  }

  @Override
  public @NotNull String parsingResult(@NotNull Lang fromLang, @NotNull Lang toLang, @NotNull String text, @NotNull String resultText) {
    LOG.info("parsingResult: " + resultText);
    return GsonUtil.getInstance().getGson().fromJson(resultText, MicrosoftTranslationResult[].class)[0].getTranslationResult();
  }

}
