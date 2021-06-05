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
