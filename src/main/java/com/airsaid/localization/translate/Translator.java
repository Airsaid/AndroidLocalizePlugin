package com.airsaid.localization.translate;

import com.airsaid.localization.translate.impl.google.GoogleTranslator;
import com.airsaid.localization.translate.lang.Lang;
import org.jetbrains.annotations.NotNull;

/**
 * The translator interface, the direct implementation class is {@link AbstractTranslator},
 * and all translators should extends {@link AbstractTranslator} to avoid writing duplicate code.
 *
 * @author airsaid
 * @see AbstractTranslator
 * @see GoogleTranslator
 */
public interface Translator {

  /**
   * Invoke translation operation.
   *
   * @param fromLang the language of text.
   * @param toLang   the language to be translated into.
   * @param text     the text to be translated.
   * @return the translated text.
   * @throws TranslationException this exception is thrown if the translation failed.
   */
  String doTranslate(@NotNull Lang fromLang, @NotNull Lang toLang, @NotNull String text) throws TranslationException;

}
