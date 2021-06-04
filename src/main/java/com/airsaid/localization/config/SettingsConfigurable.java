package com.airsaid.localization.config;

import com.airsaid.localization.constant.Constants;
import com.airsaid.localization.translate.AbstractTranslator;
import com.airsaid.localization.translate.services.TranslatorService;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.options.Configurable;
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
    LOG.info("isModified: " + isChanged);
    return isChanged;
  }

  @Override
  public void apply() {
    SettingsState settingsState = SettingsState.getInstance();
    AbstractTranslator selectedTranslator = settingsComponent.getSelectedTranslator();
    LOG.info("apply selectedTranslator: " + selectedTranslator.getName());
    settingsState.setSelectedTranslator(selectedTranslator);
    if (!settingsComponent.isSelectedDefaultTranslator()) {
      settingsState.setAppId(selectedTranslator.getKey(), settingsComponent.getAppId());
      settingsState.setAppKey(selectedTranslator.getKey(), settingsComponent.getAppKey());
    }
    settingsState.setEnableCache(settingsComponent.isEnableCache());
    settingsState.setMaxCacheSize(settingsComponent.getMaxCacheSize());

    TranslatorService translatorService = TranslatorService.getInstance();
    translatorService.setSelectedTranslator(selectedTranslator);
    translatorService.setEnableCache(settingsComponent.isEnableCache());
    translatorService.setMaxCacheSize(settingsComponent.getMaxCacheSize());
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
  }

  @Override
  public void disposeUIResources() {
    settingsComponent = null;
  }
}
