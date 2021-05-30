package com.airsaid.localization.translate;

import com.airsaid.localization.translate.impl.google.GoogleTranslationResult;
import org.jetbrains.annotations.NotNull;

/**
 * @author airsaid
 * @see GoogleTranslationResult
 */
public interface TranslationResult {

  @NotNull
  String getTranslationResult();

}
