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

package com.airsaid.localization.extensions

private val NON_TRANSLATABLE_REGEX = Regex("^[\\d\\p{P}\\p{S}\\s]+$")

/**
 * Checks if this string contains only numbers, symbols, punctuation, and whitespace.
 * This is used to determine if text needs translation - strings with only these characters
 * typically don't require translation.
 *
 * @return `true` if this string is not null, not empty, and contains only digits, punctuation,
 *         symbols, and whitespace. `false` if this string is null, empty, or contains any
 *         letters that would need translation.
 *
 * @sample
 * ```
 * "123".hasNoTranslatableText()      // returns true
 * "12.3".hasNoTranslatableText()     // returns true
 * "-123".hasNoTranslatableText()     // returns true
 * "123!@#".hasNoTranslatableText()   // returns true
 * "12 34".hasNoTranslatableText()    // returns true
 * "abc".hasNoTranslatableText()      // returns false
 * "123abc".hasNoTranslatableText()   // returns false
 * null.hasNoTranslatableText()       // returns true
 * ```
 */
fun String?.hasNoTranslatableText(): Boolean {
  if (this.isNullOrEmpty()) return true
  return this.matches(NON_TRANSLATABLE_REGEX)
}