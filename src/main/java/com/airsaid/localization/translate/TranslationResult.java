package com.airsaid.localization.translate;

import com.airsaid.localization.translate.impl.google.GoogleTranslationResult;
import org.jetbrains.annotations.NotNull;

/**
 * Translation results interface to obtain common translation result.
 *
 * @author airsaid
 * @see GoogleTranslationResult
 */
public interface TranslationResult {

  /**
   * Get a translation result of the specified text.
   *
   * @return translation result text.
   */
  @NotNull
  String getTranslationResult();

}
