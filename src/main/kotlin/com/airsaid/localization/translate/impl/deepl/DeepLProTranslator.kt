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

package com.airsaid.localization.translate.impl.deepl

import com.airsaid.localization.translate.AbstractTranslator
import com.airsaid.localization.translate.lang.Lang
import com.google.auto.service.AutoService

/**
 * @author airsaid
 */
@AutoService(AbstractTranslator::class)
class DeepLProTranslator : DeepLTranslator() {

    companion object {
        private const val KEY = "DeepLPro"
        private const val HOST_URL = "https://api.deepl.com/v2"
        private const val TRANSLATE_URL = "$HOST_URL/translate"
    }

    override val key: String = KEY

    override val name: String = KEY

    override fun getRequestUrl(fromLang: Lang, toLang: Lang, text: String): String = TRANSLATE_URL
}