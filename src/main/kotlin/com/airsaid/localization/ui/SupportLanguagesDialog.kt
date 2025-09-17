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

package com.airsaid.localization.ui

import com.airsaid.localization.translate.AbstractTranslator
import com.airsaid.localization.translate.lang.Lang
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.components.JBLabel
import java.awt.GridLayout
import javax.swing.Action
import javax.swing.JComponent
import javax.swing.JPanel

/**
 * @author airsaid
 */
class SupportLanguagesDialog(private val translator: AbstractTranslator) : DialogWrapper(true) {

    init {
        title = "${translator.name} Translator Supported Languages"
        init()
    }

    override fun createCenterPanel(): JComponent? {
        val supportedLanguages = translator.supportedLanguages.toMutableList()
        supportedLanguages.sortWith(EnglishNameComparator())
        val contentPanel = JPanel(GridLayout(supportedLanguages.size / 4, 4, 10, 20))
        for (supportedLanguage in supportedLanguages) {
            contentPanel.add(JBLabel(supportedLanguage.englishName))
        }
        return contentPanel
    }

    override fun getDimensionServiceKey(): String? {
        val key = translator.key
        return "#com.airsaid.localization.ui.SupportLanguagesDialog#$key"
    }

    override fun createActions(): Array<Action> {
        return emptyArray()
    }

    class EnglishNameComparator : Comparator<Lang> {
        override fun compare(o1: Lang, o2: Lang): Int {
            return o1.englishName.compareTo(o2.englishName)
        }
    }
}