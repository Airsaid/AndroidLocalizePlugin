/*
 * Copyright 2018 Airsaid. https://github.com/airsaid
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
 */

package com.airsaid.localization.logic;

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
 * A helper class that operates on language data.
 *
 * @author airsaid
 */
public class LanguageHelper {

    private LanguageHelper() {
        throw new AssertionError("No LanguageHelper instances for you!");
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
                .setValue(Constants.KEY_SELECTED_LANGUAGES, getLanguageCodeString(languages));
    }

    /**
     * Get saved language code data.
     *
     * @param project current project.
     * @return null if not saved.
     */
    @Nullable
    public static List<String> getSelectedLanguageCodes(@NotNull Project project) {
        Objects.requireNonNull(project);

        String codeString = PropertiesComponent.getInstance(project)
                .getValue(Constants.KEY_SELECTED_LANGUAGES);

        if (TextUtils.isEmpty(codeString)) {
            return null;
        }

        return Arrays.asList(codeString.split(","));
    }

    @NotNull
    private static String getLanguageCodeString(@NotNull List<Lang> language) {
        StringBuilder codes = new StringBuilder(language.size());
        for (int i = 0, len = language.size(); i < len; i++) {
            Lang lang = language.get(i);
            String code = lang.getCode();
            codes.append(code);
            if (i < len - 1) {
                codes.append(",");
            }
        }
        return codes.toString();
    }

}
