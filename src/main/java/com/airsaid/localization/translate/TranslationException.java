package com.airsaid.localization.translate;

import com.airsaid.localization.translate.lang.Lang;
import org.jetbrains.annotations.NotNull;

/**
 * @author airsaid
 */
public class TranslationException extends RuntimeException {
  private final Lang fromLang;
  private final Lang toLang;
  private final String text;

  public TranslationException(@NotNull Lang fromLang, @NotNull Lang toLang, @NotNull String text, String message) {
    super("Failed to translate " + text + " text from " + fromLang.getEnglishName() + " language " +
        "to " + toLang.getEnglishName() + " language: " + message);
    this.fromLang = fromLang;
    this.toLang = toLang;
    this.text = text;
  }

  public Lang getFromLang() {
    return fromLang;
  }

  public Lang getToLang() {
    return toLang;
  }

  public String getText() {
    return text;
  }
}
