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

package com.airsaid.localization.utils

import com.airsaid.localization.constant.Constants
import com.airsaid.localization.translate.lang.Lang
import com.airsaid.localization.translate.lang.Languages
import com.intellij.ide.util.PropertiesComponent
import com.intellij.openapi.project.Project
import org.apache.http.util.TextUtils

/**
 * A util class that operates on language data.
 *
 * @author airsaid
 */
object LanguageUtil {

  private const val SEPARATOR_SELECTED_LANGUAGES_CODE = ","

  /**
   * Persist the selected language codes in the current project.
   *
   * @param project current project.
   * @param languages selected language.
   */
  fun saveSelectedLanguages(project: Project, languages: List<Lang>) {
    PropertiesComponent.getInstance(project)
      .setValue(Constants.KEY_SELECTED_LANGUAGES, getLanguageCodeString(languages))
  }

  /**
   * Fetch the persisted selected language codes for the given project.
   *
   * @param project current project.
   * @return the selected language codes, or null if not set before.
   */
  fun getSelectedLanguageIds(project: Project): List<String> {
    val codeString = PropertiesComponent.getInstance(project)
      .getValue(Constants.KEY_SELECTED_LANGUAGES)

    return parseStoredLanguageCodes(codeString)
  }

  /**
   * Persist the favorite language codes in the current project.
   *
   * @param project  current project.
   * @param languages favorite language.
   */
  fun saveFavoriteLanguages(project: Project, languages: List<Lang>) {
    PropertiesComponent.getInstance(project)
      .setValue(Constants.KEY_FAVORITE_LANGUAGES, getLanguageCodeString(languages))
  }

  /**
   * Fetch the persisted favorite language codes for the given project.
   *
   * @param project current project.
   * @return the favorite language codes, or null if not set before.
   */
  fun getFavoriteLanguageIds(project: Project): List<String> {
    val codeString = PropertiesComponent.getInstance(project)
      .getValue(Constants.KEY_FAVORITE_LANGUAGES)

    return parseStoredLanguageCodes(codeString)
  }

  private fun getLanguageCodeString(language: List<Lang>): String {
    return language.joinToString(SEPARATOR_SELECTED_LANGUAGES_CODE) { it.code }
  }

  private fun parseStoredLanguageCodes(value: String?): List<String> {
    if (value.isNullOrEmpty()) return emptyList()

    return value.split(SEPARATOR_SELECTED_LANGUAGES_CODE)
      .map { it.trim() }
      .filter { it.isNotEmpty() && Languages.entries.any { lang -> lang.code == it } }
      .distinct()
  }
}
