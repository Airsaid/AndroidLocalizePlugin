package com.airsaid.localization.translate.impl.googleapi;

import com.airsaid.localization.translate.TranslationResult;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

/**
 * @author airsaid
 */
public class GoogleApiTranslationResult implements TranslationResult {
  public Data data;
  public Error error;

  public boolean isSuccess() {
    return data != null && error == null;
  }

  @Override
  public @NotNull String getTranslationResult() {
    return data.translations[0].translatedText;
  }

  @Override
  public String toString() {
    return "GoogleApiTranslationResult{" +
        "data=" + data +
        ", error=" + error +
        '}';
  }

  public static class Data {
    public Translation[] translations;

    @Override
    public String toString() {
      return "Data{" +
          "translations=" + Arrays.toString(translations) +
          '}';
    }

    public static class Translation {
      public String translatedText;
      public String detectedSourceLanguage;

      @Override
      public String toString() {
        return "Translation{" +
            "translatedText='" + translatedText + '\'' +
            ", detectedSourceLanguage='" + detectedSourceLanguage + '\'' +
            '}';
      }
    }
  }

  public static class Error {
    public int code;
    public String message;

    @Override
    public String toString() {
      return "Error{" +
          "code=" + code +
          ", message='" + message + '\'' +
          '}';
    }
  }
}