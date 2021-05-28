package com.airsaid.localization.translate.services;

import com.airsaid.localization.translate.Translator;
import com.airsaid.localization.translate.impl.google.GoogleTranslator;
import com.airsaid.localization.translate.lang.Lang;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

/**
 * @author airsaid
 */
@Service
public final class TranslatorService {

  private static final Logger LOG = Logger.getInstance(TranslatorService.class);

  private Translator translator;

  public TranslatorService() {
    translator = new GoogleTranslator();
  }

  @NotNull
  public static TranslatorService getInstance() {
    return ServiceManager.getService(TranslatorService.class);
  }

  public void setTranslator(@NotNull Translator translator) {
    LOG.info(String.format("setTranslator: %s", translator));
    this.translator = translator;
  }

  public void doTranslate(@NotNull Lang fromLang, @NotNull Lang toLang, @NotNull String text, @NotNull Consumer<String> consumer) {
    LOG.info(String.format("doTranslate fromLang: %s, toLang: %s, text: %s", fromLang, toLang, text));
    ApplicationManager.getApplication().executeOnPooledThread(() -> {
      String result = translator.doTranslate(fromLang, toLang, text);
      LOG.info(String.format("doTranslate result: %s", result));
      consumer.accept(result);
    });
  }

}
