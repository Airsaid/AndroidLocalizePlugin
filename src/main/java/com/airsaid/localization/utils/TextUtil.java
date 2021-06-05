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

package com.airsaid.localization.utils;

import com.intellij.openapi.util.text.StringUtil;
import org.jetbrains.annotations.Nullable;

/**
 * @author airsaid
 */
public class TextUtil {

  public static boolean isEmptyOrSpacesLineBreak(@Nullable CharSequence s) {
    if (StringUtil.isEmpty(s)) {
      return true;
    }
    for (int i = 0; i < s.length(); i++) {
      if (s.charAt(i) != ' ' && s.charAt(i) != '\r' && s.charAt(i) != '\n') {
        return false;
      }
    }
    return true;
  }

}
