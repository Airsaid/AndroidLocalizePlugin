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

package com.airsaid.localization.translate.lang;

import com.intellij.openapi.util.text.StringUtil;

import java.util.Objects;

/**
 * Language data class, which is an immutable class,
 * any modification to it will generate you a new object.
 *
 * @author airsaid
 */
public final class Lang implements Cloneable {
  private final int id;
  private final String code;
  private final String name;
  private final String englishName;
  private String translationCode;

  public Lang(int id, String code, String name, String englishName) {
    this.id = id;
    this.code = code;
    this.name = name;
    this.englishName = englishName;
  }

  public int getId() {
    return id;
  }

  public String getCode() {
    return code;
  }

  public String getName() {
    return name;
  }

  public String getEnglishName() {
    return englishName;
  }

  public Lang setTranslationCode(String translationCode) {
    final Lang newLang = this.clone();
    Objects.requireNonNull(newLang).translationCode = translationCode;
    return newLang;
  }

  public String getTranslationCode() {
    if (!StringUtil.isEmpty(translationCode)) {
      return translationCode;
    }
    return code;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Lang language = (Lang) o;
    return id == language.id;
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

  @Override
  public Lang clone() {
    try {
      return (Lang) super.clone();
    } catch (CloneNotSupportedException e) {
      e.printStackTrace();
    }
    return null;
  }

  @Override
  public String toString() {
    return "Lang{" +
        "id=" + id +
        ", code='" + code + '\'' +
        ", name='" + name + '\'' +
        ", englishName='" + englishName + '\'' +
        ", translationCode='" + translationCode + '\'' +
        '}';
  }
}