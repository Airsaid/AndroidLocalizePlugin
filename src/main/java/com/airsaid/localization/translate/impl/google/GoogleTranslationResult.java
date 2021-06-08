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

package com.airsaid.localization.translate.impl.google;

import com.airsaid.localization.translate.TranslationResult;
import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

/**
 * @author airsaid
 */
public class GoogleTranslationResult implements TranslationResult {
  @SerializedName("src")
  private String sourceCode;

  private List<Sentences> sentences;

  public GoogleTranslationResult() {

  }

  public GoogleTranslationResult(String sourceCode, List<Sentences> sentences) {
    this.sourceCode = sourceCode;
    this.sentences = sentences;
  }

  public String getSourceCode() {
    return sourceCode;
  }

  public void setSourceCode(String sourceCode) {
    this.sourceCode = sourceCode;
  }

  public List<Sentences> getSentences() {
    return sentences;
  }

  public void setSentences(List<Sentences> sentences) {
    this.sentences = sentences;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    GoogleTranslationResult that = (GoogleTranslationResult) o;
    return Objects.equals(sourceCode, that.sourceCode) && Objects.equals(sentences, that.sentences);
  }

  @Override
  public int hashCode() {
    return Objects.hash(sourceCode, sentences);
  }

  @Override
  public String toString() {
    return "GoogleTranslationResult{" +
        "sourceCode='" + sourceCode + '\'' +
        ", sentences=" + sentences +
        '}';
  }

  @Override
  public @NotNull String getTranslationResult() {
    List<Sentences> sentences = getSentences();
    if (sentences == null || sentences.isEmpty()) {
      return "";
    }
    StringBuilder result = new StringBuilder();
    for (Sentences sentence : sentences) {
      String trans = sentence.getTrans();
      if (trans != null) result.append(trans);
    }
    return result.toString();
  }

  public static class Sentences {
    private String trans;

    @SerializedName("orig")
    private String origin;

    private int backend;

    public Sentences() {

    }

    public Sentences(String trans, String origin, int backend) {
      this.trans = trans;
      this.origin = origin;
      this.backend = backend;
    }

    public String getTrans() {
      return trans;
    }

    public void setTrans(String trans) {
      this.trans = trans;
    }

    public String getOrigin() {
      return origin;
    }

    public void setOrigin(String origin) {
      this.origin = origin;
    }

    public int getBackend() {
      return backend;
    }

    public void setBackend(int backend) {
      this.backend = backend;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      Sentences sentences = (Sentences) o;
      return backend == sentences.backend && Objects.equals(trans, sentences.trans) && Objects.equals(origin, sentences.origin);
    }

    @Override
    public int hashCode() {
      return Objects.hash(trans, origin, backend);
    }

    @Override
    public String toString() {
      return "Sentences{" +
          "trans='" + trans + '\'' +
          ", origin='" + origin + '\'' +
          ", backend=" + backend +
          '}';
    }
  }
}
