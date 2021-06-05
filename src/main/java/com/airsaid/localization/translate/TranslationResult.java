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
