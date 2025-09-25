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

package com.airsaid.localization.translate

import com.airsaid.localization.translate.lang.Lang
import com.intellij.openapi.diagnostic.Logger

/**
 * @author airsaid
 */
class TranslationException : RuntimeException {

  companion object {
    private val LOG = Logger.getInstance(TranslationException::class.java)
  }

  val fromLang: Lang
  val toLang: Lang
  val text: String

  constructor(fromLang: Lang, toLang: Lang, text: String, cause: Throwable) : super(
    "Failed to translate \"$text\" from ${fromLang.englishName} to ${toLang.englishName} with error: ${cause.message}",
    cause
  ) {
    this.fromLang = fromLang
    this.toLang = toLang
    this.text = text
    LOG.warn("TranslationException: ${cause.message}", cause)
  }

  constructor(fromLang: Lang, toLang: Lang, text: String, message: String) : super(
    "Failed to translate \"$text\" from ${fromLang.englishName} to ${toLang.englishName} with error: $message"
  ) {
    this.fromLang = fromLang
    this.toLang = toLang
    this.text = text
    LOG.warn("TranslationException: $message")
  }
}
