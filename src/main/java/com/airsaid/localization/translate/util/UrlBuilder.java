package com.airsaid.localization.translate.util;

import com.intellij.openapi.util.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author airsaid
 */
public class UrlBuilder {

  private final String baseUrl;
  private final List<Pair<String, String>> queryParameters;

  public UrlBuilder(String baseUrl) {
    this.baseUrl = baseUrl;
    queryParameters = new ArrayList<>();
  }

  public UrlBuilder addQueryParameter(String key, String value) {
    queryParameters.add(Pair.create(key, value));
    return this;
  }

  public UrlBuilder addQueryParameters(String key, String... values) {
    queryParameters.addAll(Arrays.stream(values).map(value -> Pair.create(key, value)).collect(Collectors.toList()));
    return this;
  }

  public String build() {
    StringBuilder result = new StringBuilder(baseUrl);
    for (int i = 0; i < queryParameters.size(); i++) {
      if (i == 0) {
        result.append("?");
      } else {
        result.append("&");
      }
      Pair<String, String> param = queryParameters.get(i);
      String key = param.first;
      String value = param.second;
      result.append(key)
          .append("=")
          .append(value);
    }
    return result.toString();
  }

}
