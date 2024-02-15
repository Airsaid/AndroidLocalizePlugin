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

package com.airsaid.localization.config;

import com.airsaid.localization.services.AndroidValuesService;
import com.airsaid.localization.translate.AbstractTranslator;
import com.airsaid.localization.translate.services.TranslatorService;
import com.airsaid.localization.utils.SecureStorage;
import com.intellij.openapi.components.*;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.text.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author airsaid
 */
@State(
    name = "com.airsaid.localization.config.SettingsState",
    storages = {@Storage("androidLocalizeSettings.xml")}
)
@Service
public final class SettingsState implements PersistentStateComponent<SettingsState.State> {

  private static final Logger LOG = Logger.getInstance(SettingsState.class);

  private final Map<String, SecureStorage> appKeyStorage;

  private State state = new State();

  public SettingsState() {
    appKeyStorage = new HashMap<>();
    TranslatorService translatorService = TranslatorService.getInstance();
    Collection<AbstractTranslator> translators = translatorService.getTranslators().values();
    for (AbstractTranslator translator : translators) {
      if (translatorService.getDefaultTranslator() != translator) {
        appKeyStorage.put(translator.getKey(), new SecureStorage(translator.getKey()));
      }
    }
  }

  public static SettingsState getInstance() {
    return ServiceManager.getService(SettingsState.class);
  }

  public void initSetting() {
    TranslatorService translatorService = TranslatorService.getInstance();
    AbstractTranslator selectedTranslator = translatorService.getSelectedTranslator();
    if (selectedTranslator == null) {
      LOG.info("initSetting");
      translatorService.setSelectedTranslator(getSelectedTranslator());
      translatorService.setEnableCache(isEnableCache());
      translatorService.setMaxCacheSize(getMaxCacheSize());
      translatorService.setTranslationInterval(getTranslationInterval());
    }

    AndroidValuesService.getInstance().setSkipNonTranslatable(isSkipNonTranslatable());
  }

  public AbstractTranslator getSelectedTranslator() {
    return StringUtil.isEmpty(state.selectedTranslatorKey) ? TranslatorService.getInstance().getDefaultTranslator() :
        TranslatorService.getInstance().getTranslators().get(state.selectedTranslatorKey);
  }

  public void setSelectedTranslator(AbstractTranslator translator) {
    this.state.selectedTranslatorKey = translator.getKey();
  }

  public void setAppId(@NotNull String translatorKey, @NotNull String appId) {
    state.appIds.put(translatorKey, appId);
  }

  @NotNull
  public String getAppId(String translatorKey) {
    String appId = state.appIds.get(translatorKey);
    return appId != null ? appId : "";
  }

  public void setAppKey(@NotNull String translatorKey, @NotNull String appKey) {
    SecureStorage secureStorage = appKeyStorage.get(translatorKey);
    if (secureStorage != null) {
      secureStorage.save(appKey);
    }
  }

  @NotNull
  public String getAppKey(@NotNull String translatorKey) {
    SecureStorage secureStorage = appKeyStorage.get(translatorKey);
    return secureStorage != null ? secureStorage.read() : "";
  }

  public boolean isEnableCache() {
    return state.isEnableCache;
  }

  public void setEnableCache(boolean isEnable) {
    state.isEnableCache = isEnable;
  }

  public int getMaxCacheSize() {
    return state.maxCacheSize;
  }

  public void setMaxCacheSize(int maxCacheSize) {
    state.maxCacheSize = maxCacheSize;
  }

  public int getTranslationInterval() {
    return state.translationInterval;
  }

  public void setTranslationInterval(int intervalTime) {
    state.translationInterval = intervalTime;
  }

  public boolean isSkipNonTranslatable() {
    return state.isSkipNonTranslatable;
  }

  public void setSkipNonTranslatable(boolean isSkipNonTranslatable) {
    state.isSkipNonTranslatable = isSkipNonTranslatable;
  }

  @Override
  public @Nullable SettingsState.State getState() {
    return state;
  }

  @Override
  public void loadState(@NotNull State state) {
    this.state = state;
  }

  static class State {
    public String selectedTranslatorKey;
    public Map<String, String> appIds = new HashMap<>();
    public boolean isEnableCache = true;
    public int maxCacheSize = 500;
    public int translationInterval = 2; // 2 second
    public boolean isSkipNonTranslatable;
  }
}
