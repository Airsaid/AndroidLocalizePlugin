package com.airsaid.localization.config;

import com.airsaid.localization.translate.AbstractTranslator;
import com.airsaid.localization.translate.services.TranslatorService;
import com.airsaid.localization.utils.SecureStorage;
import com.intellij.openapi.components.*;
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
  }
}
