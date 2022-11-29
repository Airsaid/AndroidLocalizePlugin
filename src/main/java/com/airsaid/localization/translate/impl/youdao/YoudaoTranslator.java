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

package com.airsaid.localization.translate.impl.youdao;

import com.airsaid.localization.translate.AbstractTranslator;
import com.airsaid.localization.translate.TranslationException;
import com.airsaid.localization.translate.lang.Lang;
import com.airsaid.localization.translate.lang.Languages;
import com.airsaid.localization.translate.util.GsonUtil;
import com.google.auto.service.AutoService;
import com.intellij.openapi.util.Pair;
import com.intellij.util.io.RequestBuilder;
import icons.PluginIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author airsaid
 */
@SuppressWarnings(value = {"SpellCheckingInspection", "unused"})
@AutoService(AbstractTranslator.class)
public class YoudaoTranslator extends AbstractTranslator {

  private static final String KEY = "Youdao";
  private static final String HOST_URL = "https://openapi.youdao.com";
  private static final String TRANSLATE_URL = HOST_URL.concat("/api");
  private static final String APPLY_APP_ID_URL = "https://ai.youdao.com/DOCSIRMA/html/%E8%87%AA%E7%84%B6%E8%AF%AD%E8%A8%80%E7%BF%BB%E8%AF%91/API%E6%96%87%E6%A1%A3/%E6%96%87%E6%9C%AC%E7%BF%BB%E8%AF%91%E6%9C%8D%E5%8A%A1/%E6%96%87%E6%9C%AC%E7%BF%BB%E8%AF%91%E6%9C%8D%E5%8A%A1-API%E6%96%87%E6%A1%A3.html";

  private List<Lang> supportedLanguages;

  @Override
  public @NotNull String getKey() {
    return KEY;
  }

  @Override
  public @NotNull String getName() {
    return "Youdao";
  }

  @Override
  public @Nullable Icon getIcon() {
    return PluginIcons.YOUDAO_ICON;
  }

  @Override
  public @NotNull List<Lang> getSupportedLanguages() {
    if (supportedLanguages == null) {
      supportedLanguages = new ArrayList<>();
      supportedLanguages.add(Languages.CHINESE_SIMPLIFIED.setTranslationCode("zh-CHS"));
      supportedLanguages.add(Languages.ENGLISH);
      supportedLanguages.add(Languages.JAPANESE);
      supportedLanguages.add(Languages.KOREAN);
      supportedLanguages.add(Languages.FRENCH);
      supportedLanguages.add(Languages.SPANISH);
      supportedLanguages.add(Languages.ITALIAN);
      supportedLanguages.add(Languages.RUSSIAN);
      supportedLanguages.add(Languages.VIETNAMESE);
      supportedLanguages.add(Languages.GERMAN);
      supportedLanguages.add(Languages.ARABIC);
      supportedLanguages.add(Languages.INDONESIAN.setTranslationCode("id"));
      supportedLanguages.add(Languages.AFRIKAANS);
      supportedLanguages.add(Languages.BOSNIAN);
      supportedLanguages.add(Languages.BULGARIAN);
      supportedLanguages.add(Languages.CATALAN);
      supportedLanguages.add(Languages.CROATIAN);
      supportedLanguages.add(Languages.CZECH);
      supportedLanguages.add(Languages.DANISH);
      supportedLanguages.add(Languages.DUTCH);
      supportedLanguages.add(Languages.ESTONIAN);
      supportedLanguages.add(Languages.FINNISH);
      supportedLanguages.add(Languages.HAITIAN_CREOLE);
      supportedLanguages.add(Languages.HINDI);
      supportedLanguages.add(Languages.HUNGARIAN);
      supportedLanguages.add(Languages.SWAHILI);
      supportedLanguages.add(Languages.LITHUANIAN);
      supportedLanguages.add(Languages.MALAY);
      supportedLanguages.add(Languages.MALTESE);
      supportedLanguages.add(Languages.NORWEGIAN);
      supportedLanguages.add(Languages.POLISH);
      supportedLanguages.add(Languages.ROMANIAN);
      supportedLanguages.add(Languages.SERBIAN.setTranslationCode("sr-Cyrl"));
      supportedLanguages.add(Languages.SLOVAK);
      supportedLanguages.add(Languages.SLOVENIAN);
      supportedLanguages.add(Languages.SWEDISH);
      supportedLanguages.add(Languages.THAI);
      supportedLanguages.add(Languages.TURKISH);
      supportedLanguages.add(Languages.UKRAINIAN);
      supportedLanguages.add(Languages.URDU);
      supportedLanguages.add(Languages.AMHARIC);
      supportedLanguages.add(Languages.AZERBAIJANI);
      supportedLanguages.add(Languages.BANGLA);
      supportedLanguages.add(Languages.BASQUE);
      supportedLanguages.add(Languages.BELARUSIAN);
      supportedLanguages.add(Languages.CEBUANO);
      supportedLanguages.add(Languages.CORSICAN);
      supportedLanguages.add(Languages.ESPERANTO);
      supportedLanguages.add(Languages.FILIPINO.setTranslationCode("tl"));
      supportedLanguages.add(Languages.FRISIAN);
      supportedLanguages.add(Languages.GUJARATI);
      supportedLanguages.add(Languages.HAUSA);
      supportedLanguages.add(Languages.HAWAIIAN);
      supportedLanguages.add(Languages.ICELANDIC);
      supportedLanguages.add(Languages.JAVANESE.setTranslationCode("jw"));
      supportedLanguages.add(Languages.KANNADA);
      supportedLanguages.add(Languages.KAZAKH);
      supportedLanguages.add(Languages.KHMER);
      supportedLanguages.add(Languages.KURDISH);
      supportedLanguages.add(Languages.KYRGYZ);
      supportedLanguages.add(Languages.LAO);
      supportedLanguages.add(Languages.LATIN);
      supportedLanguages.add(Languages.LUXEMBOURGISH);
      supportedLanguages.add(Languages.MACEDONIAN);
      supportedLanguages.add(Languages.MALAGASY);
      supportedLanguages.add(Languages.MALAYALAM);
      supportedLanguages.add(Languages.MARATHI);
      supportedLanguages.add(Languages.MONGOLIAN);
      supportedLanguages.add(Languages.BURMESE);
      supportedLanguages.add(Languages.NEPALI);
      supportedLanguages.add(Languages.CHICHEWA);
      supportedLanguages.add(Languages.PASHTO);
      supportedLanguages.add(Languages.PUNJABI);
      supportedLanguages.add(Languages.SAMOAN);
      supportedLanguages.add(Languages.SCOTTISH_GAELIC);
      supportedLanguages.add(Languages.SOTHO);
      supportedLanguages.add(Languages.SHONA);
      supportedLanguages.add(Languages.SINDHI);
      supportedLanguages.add(Languages.SLOVENIAN);
      supportedLanguages.add(Languages.SOMALI);
      supportedLanguages.add(Languages.SUNDANESE);
      supportedLanguages.add(Languages.TAJIK);
      supportedLanguages.add(Languages.TAMIL);
      supportedLanguages.add(Languages.TELUGU);
      supportedLanguages.add(Languages.UZBEK);
      supportedLanguages.add(Languages.XHOSA);
      supportedLanguages.add(Languages.YORUBA);
      supportedLanguages.add(Languages.ZULU);
    }
    return supportedLanguages;
  }

  @Override
  public String getAppIdDisplay() {
    return "应用 ID";
  }

  @Override
  public String getAppKeyDisplay() {
    return "应用秘钥";
  }

  @Nullable
  @Override
  public String getApplyAppIdUrl() {
    return APPLY_APP_ID_URL;
  }

  @Override
  public @NotNull String getRequestUrl(@NotNull Lang fromLang, @NotNull Lang toLang, @NotNull String text) {
    return TRANSLATE_URL;
  }

  private String truncate(String q) {
    int len = q.length();
    return len <= 20 ? q : (q.substring(0, 10) + len + q.substring(len - 10, len));
  }

  private String getDigest(String string) {
    char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
    byte[] btInput = string.getBytes(StandardCharsets.UTF_8);
    try {
      MessageDigest mdInst = MessageDigest.getInstance("SHA-256");
      mdInst.update(btInput);
      byte[] md = mdInst.digest();
      int j = md.length;
      char[] str = new char[j * 2];
      int k = 0;
      for (byte byte0 : md) {
        str[k++] = hexDigits[byte0 >>> 4 & 0xf];
        str[k++] = hexDigits[byte0 & 0xf];
      }
      return new String(str);
    } catch (NoSuchAlgorithmException e) {
      return null;
    }
  }

  @Override
  public @NotNull List<Pair<String, String>> getRequestParams(@NotNull Lang fromLang, @NotNull Lang toLang, @NotNull String text) {
    String salt = String.valueOf(System.currentTimeMillis());
    String curTime = String.valueOf(System.currentTimeMillis() / 1000);
    String appId = getAppId();
    String appKey = getAppKey();
    String sign = getDigest(appId + truncate(text) + salt + curTime + appKey);
    List<Pair<String, String>> params = new ArrayList<>();
    params.add(Pair.create("from", fromLang.getTranslationCode()));
    params.add(Pair.create("to", toLang.getTranslationCode()));
    params.add(Pair.create("signType", "v3"));
    params.add(Pair.create("curtime", curTime));
    params.add(Pair.create("appKey", appId));
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
    YoudaoTranslationResult translationResult = GsonUtil.getInstance().getGson().fromJson(resultText, YoudaoTranslationResult.class);
    if (translationResult.isSuccess()) {
      return translationResult.getTranslationResult();
    } else {
      throw new TranslationException(fromLang, toLang, text, translationResult.getErrorCode());
    }
  }
}
