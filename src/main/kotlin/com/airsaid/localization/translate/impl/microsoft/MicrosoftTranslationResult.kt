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

package com.airsaid.localization.translate.impl.microsoft

import com.airsaid.localization.translate.TranslationResult

/**
 * @author airsaid
 */
data class MicrosoftTranslationResult(
  var translations: List<Translation>? = null
) : TranslationResult {

  override val translationResult: String
    get() {
      return if (!translations.isNullOrEmpty()) {
        val result = translations!![0].text
        result ?: ""
      } else {
        ""
      }
    }

  data class Translation(
    var text: String? = null,
    var to: String? = null
  )

  data class Error(
    var code: String? = null,
    var message: String? = null
  )
}