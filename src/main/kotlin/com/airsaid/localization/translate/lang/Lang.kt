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

package com.airsaid.localization.translate.lang

import com.intellij.openapi.util.text.StringUtil

/**
 * Language data class, which is an immutable class,
 * any modification to it will generate you a new object.
 *
 * @author airsaid
 */
data class Lang(
  val id: Int,
  val code: String,
  val name: String,
  val englishName: String,
  val flag: String,
  val directoryName: String,
  private val _translationCode: String? = null
) : Cloneable {

  val translationCode: String
    get() = if (!StringUtil.isEmpty(_translationCode)) _translationCode!! else code

  fun setTranslationCode(translationCode: String): Lang {
    return this.copy(_translationCode = translationCode)
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other == null || javaClass != other.javaClass) return false
    val language = other as Lang
    return id == language.id
  }

  override fun hashCode(): Int {
    return id.hashCode()
  }

  public override fun clone(): Lang {
    return try {
      super.clone() as Lang
    } catch (e: CloneNotSupportedException) {
      e.printStackTrace()
      this.copy()
    }
  }

  override fun toString(): String {
    return "Lang{" +
        "id=$id, " +
        "code='$code', " +
        "name='$name', " +
        "englishName='$englishName', " +
        "flag='$flag', " +
        "directoryName='$directoryName', " +
        "translationCode='$translationCode'" +
        '}'
  }
}