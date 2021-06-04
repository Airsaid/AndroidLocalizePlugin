package com.airsaid.localization.translate.impl.youdao;

import com.airsaid.localization.translate.TranslationResult;
import com.intellij.openapi.util.text.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

/**
 * @author airsaid
 */
public class YoudaoTranslationResult implements TranslationResult {
  private String requestId;
  private String errorCode;
  private List<String> translation;

  public String getRequestId() {
    return requestId;
  }

  public void setRequestId(String requestId) {
    this.requestId = requestId;
  }

  public String getErrorCode() {
    return errorCode;
  }

  public void setErrorCode(String errorCode) {
    this.errorCode = errorCode;
  }

  public List<String> getTranslation() {
    return translation;
  }

  public void setTranslation(List<String> translation) {
    this.translation = translation;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    YoudaoTranslationResult that = (YoudaoTranslationResult) o;
    return Objects.equals(requestId, that.requestId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(requestId);
  }

  public boolean isSuccess() {
    String errorCode = getErrorCode();
    return !StringUtil.isEmpty(errorCode) && "0".equals(errorCode);
  }

  @Override
  public @NotNull String getTranslationResult() {
    List<String> translation = getTranslation();
    if (translation != null) {
      String result = translation.get(0);
      return result != null ? result : "";
    }
    return "";
  }

  @Override
  public String toString() {
    return "YoudaoTranslationResult{" +
        "requestId='" + requestId + '\'' +
        ", errorCode='" + errorCode + '\'' +
        ", translation=" + translation +
        '}';
  }
}
