package com.airsaid.localization.translate;

import com.airsaid.localization.translate.impl.google.GoogleTranslator;
import com.airsaid.localization.translate.lang.Lang;
import org.jetbrains.annotations.NotNull;

/**
 * @author airsaid
 * @see GoogleTranslator
 */
public interface Translator {

  String doTranslate(@NotNull Lang fromLang, @NotNull Lang toLang, @NotNull String text) throws TranslationException;

}
