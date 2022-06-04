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

package com.airsaid.localization.translate.interceptors;

import com.airsaid.localization.translate.services.TranslatorService;
import com.intellij.openapi.util.text.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author airsaid
 */
public class EscapeCharactersInterceptor implements TranslatorService.TranslationInterceptor {

  private final List<Character> needEscapeChars = new ArrayList<>();

  public EscapeCharactersInterceptor() {
    needEscapeChars.add('@');
    needEscapeChars.add('?');
    needEscapeChars.add('\'');
    needEscapeChars.add('\"');
  }

  @Override
  public String process(String text) {
    if (StringUtil.isEmpty(text)) {
      return text;
    }
    final StringBuilder result = new StringBuilder();
    final char[] chars = text.toCharArray();
    for (char ch : chars) {
      if (needEscapeChars.contains(ch)) {
        result.append('\\');
      }
      result.append(ch);
    }
    return result.toString();
  }
}