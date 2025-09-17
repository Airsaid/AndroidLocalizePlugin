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
     * Save the language data selected in the current project.
     *
     * @param project   current project.
     * @param languages selected language.
     */
    fun saveSelectedLanguage(project: Project, languages: List<Lang>) {
        PropertiesComponent.getInstance(project)
            .setValue(Constants.KEY_SELECTED_LANGUAGES, getLanguageIdString(languages))
    }

    /**
     * Get saved language code data in the current project.
     *
     * @param project current project.
     * @return null if not saved, otherwise return the saved language id data.
     */
    fun getSelectedLanguageIds(project: Project?): List<String>? {
        val codeString = PropertiesComponent.getInstance(project!!)
            .getValue(Constants.KEY_SELECTED_LANGUAGES)

        if (TextUtils.isEmpty(codeString)) {
            return null
        }

        return codeString!!.split(SEPARATOR_SELECTED_LANGUAGES_CODE)
    }

    private fun getLanguageIdString(language: List<Lang>): String {
        return language.joinToString(SEPARATOR_SELECTED_LANGUAGES_CODE) { it.id.toString() }
    }
}