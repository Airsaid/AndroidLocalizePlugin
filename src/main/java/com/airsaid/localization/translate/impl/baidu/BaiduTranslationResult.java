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

package com.airsaid.localization.translate.impl.baidu;

import com.airsaid.localization.translate.TranslationResult;
import com.google.gson.annotations.SerializedName;
import com.intellij.openapi.util.text.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

/**
 * @author airsaid
 */
public class BaiduTranslationResult implements TranslationResult {
  private String from;
  private String to;
  @SerializedName("trans_result")
  private List<Content> contents;

  @SerializedName("error_code")
  private String errorCode;
  @SerializedName("error_msg")
  private String errorMsg;

  public String getFrom() {
    return from;
  }

  public void setFrom(String from) {
    this.from = from;
  }

  public String getTo() {
    return to;
  }

  public void setTo(String to) {
    this.to = to;
  }

  public List<Content> getContents() {
    return contents;
  }

  public void setContents(List<Content> contents) {
    this.contents = contents;
  }

  public String getErrorCode() {
    return errorCode;
  }

  public void setErrorCode(String errorCode) {
    this.errorCode = errorCode;
  }

  public String getErrorMsg() {
    return errorMsg;
  }

  public void setErrorMsg(String errorMsg) {
    this.errorMsg = errorMsg;
  }

  public boolean isSuccess() {
    String errorCode = getErrorCode();
    return StringUtil.isEmpty(errorCode) || "52000".equals(getErrorCode());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    BaiduTranslationResult that = (BaiduTranslationResult) o;
    return Objects.equals(from, that.from) && Objects.equals(to, that.to) && Objects.equals(contents, that.contents) && Objects.equals(errorCode, that.errorCode) && Objects.equals(errorMsg, that.errorMsg);
  }

  @Override
  public int hashCode() {
    return Objects.hash(from, to, contents, errorCode, errorMsg);
  }

  @Override
  public @NotNull String getTranslationResult() {
    List<Content> contents = getContents();
    if (contents == null || contents.isEmpty()) {
      return "";
    }
    String dst = contents.get(0).getDst();
    return dst != null ? dst : "";
  }

  @Override
  public String toString() {
    return "BaiduTranslationResult{" +
        "from='" + from + '\'' +
        ", to='" + to + '\'' +
        ", contents=" + contents +
        ", errorCode='" + errorCode + '\'' +
        ", errorMsg='" + errorMsg + '\'' +
        '}';
  }

  public static class Content {
    private String src;
    private String dst;

    public String getSrc() {
      return src;
    }

    public void setSrc(String src) {
      this.src = src;
    }

    public String getDst() {
      return dst;
    }

    public void setDst(String dst) {
      this.dst = dst;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      Content content = (Content) o;
      return Objects.equals(src, content.src) && Objects.equals(dst, content.dst);
    }

    @Override
    public int hashCode() {
      return Objects.hash(src, dst);
    }

    @Override
    public String toString() {
      return "Content{" +
          "src='" + src + '\'' +
          ", dst='" + dst + '\'' +
          '}';
    }
  }
}
