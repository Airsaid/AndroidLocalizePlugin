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

package com.airsaid.localization.translate.impl.google

import com.airsaid.localization.translate.AbstractTranslator
import com.airsaid.localization.translate.TranslatorCredentialDescriptor
import com.airsaid.localization.translate.lang.Lang
import com.airsaid.localization.translate.lang.Languages
import icons.PluginIcons
import javax.swing.Icon

/**
 * @author airsaid
 */
abstract class AbsGoogleTranslator : AbstractTranslator() {

    protected var _supportedLanguages: MutableList<Lang>? = null

    override val icon: Icon = PluginIcons.GOOGLE_ICON

    override val credentialDefinitions: List<TranslatorCredentialDescriptor> = emptyList()

    override val supportedLanguages: List<Lang>
        get() {
            if (_supportedLanguages == null) {
                val languages = Languages.getLanguages()
                _supportedLanguages = mutableListOf<Lang>().apply {
                    for (i in 1..104) {
                        var lang = languages[i]
                        lang = when (lang) {
                            Languages.CHINESE_SIMPLIFIED -> lang.setTranslationCode("zh-CN")
                            Languages.CHINESE_TRADITIONAL -> lang.setTranslationCode("zh-TW")
                            Languages.FILIPINO -> lang.setTranslationCode("tl")
                            Languages.INDONESIAN -> lang.setTranslationCode("id")
                            Languages.JAVANESE -> lang.setTranslationCode("jw")
                            else -> lang
                        }
                        add(lang)
                    }
                }
            }
            return _supportedLanguages!!
        }
}
