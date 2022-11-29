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

package com.airsaid.localization.translate.impl.baidu;

import com.airsaid.localization.translate.AbstractTranslator;
import com.airsaid.localization.translate.TranslationException;
import com.airsaid.localization.translate.lang.Lang;
import com.airsaid.localization.translate.lang.Languages;
import com.airsaid.localization.translate.util.GsonUtil;
import com.airsaid.localization.translate.util.MD5;
import com.google.auto.service.AutoService;
import com.intellij.openapi.util.Pair;
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
public class BaiduTranslator extends AbstractTranslator {
  private static final String KEY = "Baidu";
  private static final String HOST_URL = "http://api.fanyi.baidu.com";
  private static final String TRANSLATE_URL = HOST_URL.concat("/api/trans/vip/translate");
  private static final String APPLY_APP_ID_URL = "http://api.fanyi.baidu.com/api/trans/product/desktop?req=developer";

  private List<Lang> supportedLanguages;

  @Override
  public @NotNull String getKey() {
    return KEY;
  }

  @Override
  public @NotNull String getName() {
    return "Baidu";
  }

  @Override
  public @Nullable Icon getIcon() {
    return PluginIcons.BAIDU_ICON;
  }

  @Override
  public @NotNull List<Lang> getSupportedLanguages() {
    if (supportedLanguages == null) {
      supportedLanguages = new ArrayList<>();
      supportedLanguages.add(Languages.CHINESE_SIMPLIFIED.setTranslationCode("zh"));
      supportedLanguages.add(Languages.ENGLISH);
      supportedLanguages.add(Languages.JAPANESE.setTranslationCode("jp"));
      supportedLanguages.add(Languages.KOREAN.setTranslationCode("kor"));
      supportedLanguages.add(Languages.FRENCH.setTranslationCode("fra"));
      supportedLanguages.add(Languages.SPANISH.setTranslationCode("spa"));
      supportedLanguages.add(Languages.THAI);
      supportedLanguages.add(Languages.ARABIC.setTranslationCode("ara"));
      supportedLanguages.add(Languages.RUSSIAN);
      supportedLanguages.add(Languages.PORTUGUESE);
      supportedLanguages.add(Languages.GERMAN);
      supportedLanguages.add(Languages.ITALIAN);
      supportedLanguages.add(Languages.GREEK);
      supportedLanguages.add(Languages.DUTCH);
      supportedLanguages.add(Languages.POLISH);
      supportedLanguages.add(Languages.BULGARIAN.setTranslationCode("bul"));
      supportedLanguages.add(Languages.ESTONIAN.setTranslationCode("est"));
      supportedLanguages.add(Languages.DANISH.setTranslationCode("dan"));
      supportedLanguages.add(Languages.FINNISH.setTranslationCode("fin"));
      supportedLanguages.add(Languages.CZECH);
      supportedLanguages.add(Languages.ROMANIAN.setTranslationCode("rom"));
      supportedLanguages.add(Languages.SLOVENIAN.setTranslationCode("slo"));
      supportedLanguages.add(Languages.SWEDISH.setTranslationCode("swe"));
      supportedLanguages.add(Languages.HUNGARIAN);
      supportedLanguages.add(Languages.CHINESE_TRADITIONAL.setTranslationCode("cht"));
      supportedLanguages.add(Languages.VIETNAMESE.setTranslationCode("vie"));
    }
    return supportedLanguages;
  }

  @Override
  public @Nullable String getApplyAppIdUrl() {
    return APPLY_APP_ID_URL;
  }

  @Override
  public @NotNull String getRequestUrl(@NotNull Lang fromLang, @NotNull Lang toLang, @NotNull String text) {
    return TRANSLATE_URL;
  }

  @Override
  public @NotNull List<Pair<String, String>> getRequestParams(@NotNull Lang fromLang, @NotNull Lang toLang, @NotNull String text) {
    String salt = String.valueOf(System.currentTimeMillis());
    String appId = getAppId();
    String securityKey = getAppKey();
    String sign = MD5.md5(appId + text + salt + securityKey);
    List<Pair<String, String>> params = new ArrayList<>();
    params.add(Pair.create("from", fromLang.getTranslationCode()));
    params.add(Pair.create("to", toLang.getTranslationCode()));
    params.add(Pair.create("appid", appId));
    params.add(Pair.create("salt", salt));
    params.add(Pair.create("sign", sign));
    params.add(Pair.create("q", text));
    return params;
  }

  @Override
  public void configureRequestBuilder(@NotNull RequestBuilder requestBuilder) {
    requestBuilder.tuner(connection -> connection.setRequestProperty("Referer", HOST_URL));
  }

  @Override
  public @NotNull String parsingResult(@NotNull Lang fromLang, @NotNull Lang toLang, @NotNull String text, @NotNull String resultText) {
    LOG.info("parsingResult: " + resultText);
    BaiduTranslationResult baiduTranslationResult = GsonUtil.getInstance().getGson().fromJson(resultText, BaiduTranslationResult.class);
    if (baiduTranslationResult.isSuccess()) {
      return baiduTranslationResult.getTranslationResult();
    } else {
      String message = baiduTranslationResult.getErrorMsg().concat("(").concat(baiduTranslationResult.getErrorCode()).concat(")");
      throw new TranslationException(fromLang, toLang, text, message);
    }
  }
}
