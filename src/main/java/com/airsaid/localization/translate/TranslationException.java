/*
 * Copyright 2021 Airsaid. https://github.com/airsaid
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.airsaid.localization.translate;

import com.airsaid.localization.translate.lang.Lang;
import com.intellij.openapi.diagnostic.Logger;

import org.apache.http.HttpException;
import org.jetbrains.annotations.NotNull;

/**
 * @author airsaid
 */
public class TranslationException extends RuntimeException {

  private static final Logger LOG = Logger.getInstance(TranslationException.class);

  private final Lang fromLang;
  private final Lang toLang;
  private final String text;

  public TranslationException(@NotNull Lang fromLang, @NotNull Lang toLang, @NotNull String text, @NotNull Throwable cause) {
    super("Failed to translate \"" + text + "\" from " + fromLang.getEnglishName() +
            " to " + toLang.getEnglishName() + " with error: " + cause.getMessage(), cause);
    this.fromLang = fromLang;
    this.toLang = toLang;
    this.text = text;
    cause.printStackTrace();
    LOG.error("TranslationException: " + cause.getMessage(), cause);
  }

  public TranslationException(@NotNull Lang fromLang, @NotNull Lang toLang, @NotNull String text, String message) {
    super("Failed to translate \"" + text + "\" from " + fromLang.getEnglishName() +
        " to " + toLang.getEnglishName() + " with error: " + message);
    this.fromLang = fromLang;
    this.toLang = toLang;
    this.text = text;
    LOG.error("TranslationException: ", message);
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
