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

package com.airsaid.localization.translate.impl.youdao

import com.airsaid.localization.translate.TranslationResult

/**
 * @author airsaid
 */
data class YoudaoTranslationResult(
  var requestId: String? = null,
  var errorCode: String? = null,
  var translation: List<String>? = null
) : TranslationResult {

  val isSuccess: Boolean
    get() {
      val errorCode = this.errorCode
      return !errorCode.isNullOrEmpty() && "0" == errorCode
    }

  override val translationResult: String
    get() {
      val translation = this.translation
      return if (translation != null && translation.isNotEmpty()) {
        translation[0]
      } else {
        ""
      }
    }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other == null || javaClass != other.javaClass) return false
    val that = other as YoudaoTranslationResult
    return requestId == that.requestId
  }

  override fun hashCode(): Int {
    return requestId?.hashCode() ?: 0
  }

  override fun toString(): String {
    return "YoudaoTranslationResult{" +
        "requestId='$requestId', " +
        "errorCode='$errorCode', " +
        "translation=$translation" +
        '}'
  }
}