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

package com.airsaid.localization.translate.impl.ali;

import com.airsaid.localization.translate.AbstractTranslator;
import com.airsaid.localization.translate.TranslationException;
import com.airsaid.localization.translate.lang.Lang;
import com.airsaid.localization.translate.lang.Languages;
import com.aliyun.alimt20181012.Client;
import com.aliyun.alimt20181012.models.TranslateGeneralRequest;
import com.aliyun.alimt20181012.models.TranslateGeneralResponse;
import com.aliyun.alimt20181012.models.TranslateGeneralResponseBody;
import com.aliyun.teaopenapi.models.Config;
import com.aliyun.teautil.models.RuntimeOptions;
import com.google.auto.service.AutoService;
import icons.PluginIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.LinkedList;
import java.util.List;

/**
 * @author airsaid
 */
@AutoService(AbstractTranslator.class)
public class AliTranslator extends AbstractTranslator {
  private static final String KEY = "Ali";
  private static final String ENDPOINT = "mt.aliyuncs.com";
  private static final String APPLY_APP_ID_URL = "https://www.aliyun.com/product/ai/base_alimt";

  private final Config config = new Config();
  private List<Lang> supportedLanguages;
  private Client client;

  @Override
  public @NotNull String getKey() {
    return KEY;
  }

  @Override
  public @NotNull String getName() {
    return "Ali";
  }

  @Override
  public @Nullable Icon getIcon() {
    return PluginIcons.ALI_ICON;
  }

  @Override
  public @NotNull List<Lang> getSupportedLanguages() {
    if (supportedLanguages == null) {
      supportedLanguages = new LinkedList<>();
      final List<Lang> languages = Languages.getLanguages();
      for (int i = 1; i < languages.size(); i++) {
        Lang lang = languages.get(i);
        if (lang.equals(Languages.UKRAINIAN) || lang.equals(Languages.DARI)) {
          continue;
        }
        if (lang.equals(Languages.CHINESE_SIMPLIFIED)) {
          lang = lang.setTranslationCode("zh");
        } else if (lang.equals(Languages.CHINESE_TRADITIONAL)) {
          lang = lang.setTranslationCode("zh-tw");
        } else if (lang.equals(Languages.INDONESIAN)) {
          lang = lang.setTranslationCode("id");
        } else if (lang.equals(Languages.CROATIAN)) {
          lang = lang.setTranslationCode("hbs");
        } else if (lang.equals(Languages.HEBREW)) {
          lang = lang.setTranslationCode("he");
        }
        supportedLanguages.add(lang);
      }
    }
    return supportedLanguages;
  }

  @Override
  public String getAppIdDisplay() {
    return "AccessKey ID";
  }

  @Override
  public String getAppKeyDisplay() {
    return "AccessKey Secret";
  }

  @Override
  public @Nullable String getApplyAppIdUrl() {
    return APPLY_APP_ID_URL;
  }

  @Override
  public String doTranslate(@NotNull Lang fromLang, @NotNull Lang toLang, @NotNull String text) throws TranslationException {
    checkSupportedLanguages(fromLang, toLang, text);

    config.setAccessKeyId(getAppId()).setAccessKeySecret(getAppKey()).setEndpoint(ENDPOINT);
    if (client == null) {
      try {
        client = new Client(config);
      } catch (Exception e) {
        throw new TranslationException(fromLang, toLang, text, e);
      }
    }

    TranslateGeneralRequest request = new TranslateGeneralRequest()
        .setFormatType("text")
        .setSourceLanguage(fromLang.getTranslationCode())
        .setTargetLanguage(toLang.getTranslationCode())
        .setSourceText(text)
        .setScene("general");
    RuntimeOptions runtime = new RuntimeOptions();
    TranslateGeneralResponse response;
    try {
      response = client.translateGeneralWithOptions(request, runtime);
    } catch (Exception e) {
      throw new TranslationException(fromLang, toLang, text, e);
    }
    final TranslateGeneralResponseBody body = response.body;
    if (body.getCode() == 200) {
      return body.getData().translated;
    } else {
      throw new TranslationException(fromLang, toLang, text, body.getMessage() + "(" + body.getCode() + ")");
    }
  }
}
