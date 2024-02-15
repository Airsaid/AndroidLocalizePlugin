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

import com.airsaid.localization.constant.Constants;
import com.airsaid.localization.services.AndroidValuesService;
import com.airsaid.localization.translate.AbstractTranslator;
import com.airsaid.localization.translate.services.TranslatorService;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.util.text.StringUtil;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Map;

/**
 * @author airsaid
 */
public class SettingsConfigurable implements Configurable {

  private static final Logger LOG = Logger.getInstance(SettingsConfigurable.class);

  private SettingsComponent settingsComponent;

  @Override
  public String getDisplayName() {
    return Constants.PLUGIN_NAME;
  }

  @Override
  public JComponent getPreferredFocusedComponent() {
    return settingsComponent.getPreferredFocusedComponent();
  }

  @Override
  public @Nullable JComponent createComponent() {
    settingsComponent = new SettingsComponent();
    initComponents();
    return settingsComponent.getContent();
  }

  private void initComponents() {
    SettingsState settingsState = SettingsState.getInstance();
    Map<String, AbstractTranslator> translators = TranslatorService.getInstance().getTranslators();
    settingsComponent.setTranslators(translators);
    settingsComponent.setSelectedTranslator(translators.get(settingsState.getSelectedTranslator().getKey()));
    settingsComponent.setEnableCache(settingsState.isEnableCache());
    settingsComponent.setMaxCacheSize(settingsState.getMaxCacheSize());
    settingsComponent.setTranslationInterval(settingsState.getTranslationInterval());
    settingsComponent.setSkipNonTranslatable(settingsState.isSkipNonTranslatable());
  }

  @Override
  public boolean isModified() {
    SettingsState settingsState = SettingsState.getInstance();
    AbstractTranslator selectedTranslator = settingsComponent.getSelectedTranslator();
    boolean isChanged = settingsState.getSelectedTranslator() == selectedTranslator;
    isChanged |= settingsState.getAppId(selectedTranslator.getKey()).equals(selectedTranslator.getAppId());
    isChanged |= settingsState.getAppKey(selectedTranslator.getKey()).equals(selectedTranslator.getAppKey());
    isChanged |= settingsState.isEnableCache() == settingsComponent.isEnableCache();
    isChanged |= settingsState.getMaxCacheSize() == settingsComponent.getMaxCacheSize();
    isChanged |= settingsState.getTranslationInterval() == settingsComponent.getTranslationInterval();
    isChanged |= settingsState.isSkipNonTranslatable() == settingsComponent.isSkipNonTranslatable();
    LOG.info("isModified: " + isChanged);
    return isChanged;
  }

  @Override
  public void apply() throws ConfigurationException {
    SettingsState settingsState = SettingsState.getInstance();
    AbstractTranslator selectedTranslator = settingsComponent.getSelectedTranslator();
    LOG.info("apply selectedTranslator: " + selectedTranslator.getName());

    // Verify that the required parameters are not configured
    if (selectedTranslator.isNeedAppId() && StringUtil.isEmpty(settingsComponent.getAppId())) {
      throw new ConfigurationException(selectedTranslator.getAppIdDisplay() + " not configured");
    }
    if (selectedTranslator.isNeedAppKey() && StringUtil.isEmpty(settingsComponent.getAppKey())) {
      throw new ConfigurationException(selectedTranslator.getAppKeyDisplay() + " not configured");
    }

    settingsState.setSelectedTranslator(selectedTranslator);
    if (selectedTranslator.isNeedAppId()) {
      settingsState.setAppId(selectedTranslator.getKey(), settingsComponent.getAppId());
    }
    if (selectedTranslator.isNeedAppKey()) {
      settingsState.setAppKey(selectedTranslator.getKey(), settingsComponent.getAppKey());
    }
    settingsState.setEnableCache(settingsComponent.isEnableCache());
    settingsState.setMaxCacheSize(settingsComponent.getMaxCacheSize());
    settingsState.setTranslationInterval(settingsComponent.getTranslationInterval());
    settingsState.setSkipNonTranslatable(settingsComponent.isSkipNonTranslatable());

    TranslatorService translatorService = TranslatorService.getInstance();
    translatorService.setSelectedTranslator(selectedTranslator);
    translatorService.setEnableCache(settingsComponent.isEnableCache());
    translatorService.setMaxCacheSize(settingsComponent.getMaxCacheSize());
    translatorService.setTranslationInterval(settingsComponent.getTranslationInterval());

    AndroidValuesService.getInstance().setSkipNonTranslatable(settingsComponent.isSkipNonTranslatable());
  }

  @Override
  public void reset() {
    LOG.info("reset");
    SettingsState settingsState = SettingsState.getInstance();
    AbstractTranslator selectedTranslator = settingsState.getSelectedTranslator();
    settingsComponent.setSelectedTranslator(selectedTranslator);
    settingsComponent.setAppId(settingsState.getAppId(selectedTranslator.getKey()));
    settingsComponent.setAppKey(settingsState.getAppKey(selectedTranslator.getKey()));
    settingsComponent.setEnableCache(settingsState.isEnableCache());
    settingsComponent.setMaxCacheSize(settingsState.getMaxCacheSize());
    settingsComponent.setTranslationInterval(settingsState.getTranslationInterval());
    settingsComponent.setSkipNonTranslatable(settingsState.isSkipNonTranslatable());
  }

  @Override
  public void disposeUIResources() {
    settingsComponent = null;
  }
}
