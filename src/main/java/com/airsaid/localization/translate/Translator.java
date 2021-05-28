package com.airsaid.localization.translate;

import com.airsaid.localization.translate.lang.Lang;
import com.airsaid.localization.translate.trans.impl.GoogleTranslator;
import org.jetbrains.annotations.NotNull;

/**
 * @author airsaid
 * @see GoogleTranslator
 */
public interface Translator {

  String doTranslate(@NotNull Lang fromLang, @NotNull Lang toLang, @NotNull String text) throws TranslationException;

}
