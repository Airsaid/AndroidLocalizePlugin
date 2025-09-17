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

import com.airsaid.localization.translate.TranslationResult
import com.google.gson.annotations.SerializedName

/**
 * @author airsaid
 */
data class GoogleTranslationResult(
    @SerializedName("src")
    var sourceCode: String? = null,
    var sentences: List<Sentences>? = null
) : TranslationResult {

    override val translationResult: String
        get() {
            val sentences = this.sentences
            if (sentences.isNullOrEmpty()) {
                return ""
            }
            val result = StringBuilder()
            for (sentence in sentences) {
                val trans = sentence.trans
                if (trans != null) result.append(trans)
            }
            return result.toString()
        }

    data class Sentences(
        var trans: String? = null,
        @SerializedName("orig")
        var origin: String? = null,
        var backend: Int = 0
    )
}