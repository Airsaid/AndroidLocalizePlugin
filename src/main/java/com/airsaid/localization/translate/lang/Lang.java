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

import java.util.Objects;

/**
 * @author airsaid
 */
public class Lang implements Cloneable {
  private final int id;
  private String code;
  private String name;
  private String englishName;

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

  public Lang setCode(String code) {
    this.code = code;
    return this;
  }

  public String getName() {
    return name;
  }

  public Lang setName(String name) {
    this.name = name;
    return this;
  }

  public String getEnglishName() {
    return englishName;
  }

  public Lang setEnglishName(String englishName) {
    this.englishName = englishName;
    return this;
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
        '}';
  }
}