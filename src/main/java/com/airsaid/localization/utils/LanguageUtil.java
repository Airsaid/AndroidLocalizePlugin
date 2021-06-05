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

import com.airsaid.localization.constant.Constants;
import com.airsaid.localization.translate.lang.Lang;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.project.Project;
import org.apache.http.util.TextUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * A util class that operates on language data.
 *
 * @author airsaid
 */
public class LanguageUtil {

  private static final String SEPARATOR_SELECTED_LANGUAGES_CODE = ",";

  private LanguageUtil() {
    throw new AssertionError("No com.airsaid.localization.utils.LanguageUtil instances for you!");
  }

  /**
   * Save the language data selected in the current project.
   *
   * @param project   current project.
   * @param languages selected language.
   */
  public static void saveSelectedLanguage(@NotNull Project project, @NotNull List<Lang> languages) {
    Objects.requireNonNull(project);
    Objects.requireNonNull(languages);

    PropertiesComponent.getInstance(project)
        .setValue(Constants.KEY_SELECTED_LANGUAGES, getLanguageIdString(languages));
  }

  /**
   * Get saved language code data in the current project.
   *
   * @param project current project.
   * @return null if not saved, otherwise return the saved language id data.
   */
  @Nullable
  public static List<String> getSelectedLanguageIds(@NotNull Project project) {
    Objects.requireNonNull(project);

    String codeString = PropertiesComponent.getInstance(project)
        .getValue(Constants.KEY_SELECTED_LANGUAGES);

    if (TextUtils.isEmpty(codeString)) {
      return null;
    }

    return Arrays.asList(codeString.split(SEPARATOR_SELECTED_LANGUAGES_CODE));
  }

  @NotNull
  private static String getLanguageIdString(@NotNull List<Lang> language) {
    StringBuilder codes = new StringBuilder(language.size());
    for (int i = 0, len = language.size(); i < len; i++) {
      Lang lang = language.get(i);
      codes.append(lang.getId());
      if (i < len - 1) {
        codes.append(SEPARATOR_SELECTED_LANGUAGES_CODE);
      }
    }
    return codes.toString();
  }

}
