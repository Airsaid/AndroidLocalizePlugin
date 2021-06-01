package com.airsaid.localization.translate.services;

import com.airsaid.localization.translate.AbstractTranslator;
import com.airsaid.localization.translate.impl.google.GoogleTranslator;
import com.airsaid.localization.translate.lang.Lang;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;
import org.apache.log4j.Level;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

/**
 * @author airsaid
 */
@Service
public final class TranslatorService {

  private static final Logger LOG = Logger.getInstance(TranslatorService.class);

  private AbstractTranslator translator;
  private final TranslationCacheService cacheService;

  static {
    LOG.setLevel(Level.DEBUG);
  }

  public TranslatorService() {
    translator = new GoogleTranslator();
    cacheService = TranslationCacheService.getInstance();
  }

  @NotNull
  public static TranslatorService getInstance() {
    return ServiceManager.getService(TranslatorService.class);
  }

  public void setTranslator(@NotNull AbstractTranslator translator) {
    LOG.info(String.format("setTranslator: %s", translator));
    this.translator = translator;
  }

  @NotNull
  public AbstractTranslator getTranslator() {
    return translator;
  }

  public void doTranslateByAsync(@NotNull Lang fromLang, @NotNull Lang toLang, @NotNull String text, @NotNull Consumer<String> consumer) {
    ApplicationManager.getApplication().executeOnPooledThread(() -> {
      final String translatedText = doTranslate(fromLang, toLang, text);
      ApplicationManager.getApplication().invokeLater(() ->
          consumer.accept(translatedText));
    });
  }

  public String doTranslate(@NotNull Lang fromLang, @NotNull Lang toLang, @NotNull String text) {
    LOG.info(String.format("doTranslate fromLang: %s, toLang: %s, text: %s", fromLang, toLang, text));

    String cacheResult = cacheService.get(getKey(fromLang, toLang, text));
    if (!cacheResult.isEmpty()) {
      LOG.info(String.format("doTranslate cache result: %s", cacheResult));
      return cacheResult;
    }

    String result = translator.doTranslate(fromLang, toLang, text);
    LOG.info(String.format("doTranslate result: %s", result));
    cacheService.put(getKey(fromLang, toLang, text), result);
    return result;
  }

  private String getKey(@NotNull Lang fromLang, @NotNull Lang toLang, @NotNull String text) {
    return fromLang.getCode() + "_" + toLang.getCode() + "_" + text;
  }

}
