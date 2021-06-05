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

package com.airsaid.localization.translate.impl.microsoft;

import com.airsaid.localization.translate.TranslationResult;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

/**
 * @author airsaid
 */
public class MicrosoftTranslationResult implements TranslationResult {

  private List<Translation> translations;

  @Override
  public @NotNull String getTranslationResult() {
    if (translations != null && !translations.isEmpty()) {
      String result = translations.get(0).getText();
      return result != null ? result : "";
    }
    return "";
  }

  public List<Translation> getTranslations() {
    return translations;
  }

  public void setTranslations(List<Translation> translations) {
    this.translations = translations;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    MicrosoftTranslationResult that = (MicrosoftTranslationResult) o;
    return Objects.equals(translations, that.translations);
  }

  @Override
  public int hashCode() {
    return Objects.hash(translations);
  }

  @Override
  public String toString() {
    return "MicrosoftTranslationResult{" +
        "translations=" + translations +
        '}';
  }

  public static class Translation {
    private String text;
    private String to;

    public String getText() {
      return text;
    }

    public void setText(String text) {
      this.text = text;
    }

    public String getTo() {
      return to;
    }

    public void setTo(String to) {
      this.to = to;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      Translation that = (Translation) o;
      return Objects.equals(text, that.text) && Objects.equals(to, that.to);
    }

    @Override
    public int hashCode() {
      return Objects.hash(text, to);
    }

    @Override
    public String toString() {
      return "Translation{" +
          "text='" + text + '\'' +
          ", to='" + to + '\'' +
          '}';
    }
  }

  public static class Error {
    private String code;
    private String message;

    public String getCode() {
      return code;
    }

    public void setCode(String code) {
      this.code = code;
    }

    public String getMessage() {
      return message;
    }

    public void setMessage(String message) {
      this.message = message;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      Error error = (Error) o;
      return Objects.equals(code, error.code) && Objects.equals(message, error.message);
    }

    @Override
    public int hashCode() {
      return Objects.hash(code, message);
    }

    @Override
    public String toString() {
      return "Error{" +
          "code='" + code + '\'' +
          ", message='" + message + '\'' +
          '}';
    }
  }
}
