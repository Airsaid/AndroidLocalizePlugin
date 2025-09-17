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

package com.airsaid.localization.translate.impl.baidu

import com.airsaid.localization.translate.TranslationResult
import com.google.gson.annotations.SerializedName
import com.intellij.openapi.util.text.StringUtil

/**
 * @author airsaid
 */
data class BaiduTranslationResult(
    var from: String? = null,
    var to: String? = null,
    @SerializedName("trans_result")
    var contents: List<Content>? = null,
    @SerializedName("error_code")
    var errorCode: String? = null,
    @SerializedName("error_msg")
    var errorMsg: String? = null
) : TranslationResult {

    fun isSuccess(): Boolean {
        val errorCode = this.errorCode
        return StringUtil.isEmpty(errorCode) || "52000" == errorCode
    }

    override val translationResult: String
        get() {
            val contents = this.contents
            if (contents.isNullOrEmpty()) {
                return ""
            }
            val dst = contents[0].dst
            return dst ?: ""
        }

    data class Content(
        var src: String? = null,
        var dst: String? = null
    )
}