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

package com.airsaid.localization.translate.impl.openai

data class OpenAIResponse(
    var choices: List<Choice>?,
    var created: Int?,
    var id: String?,
    var `object`: String?,
    var usage: Usage?
) {
    val translation: String
        get() {
            return if (!choices.isNullOrEmpty()) {
                val result = choices!![0].message?.content
                result?.trim() ?: ""
            } else {
                ""
            }
        }

    data class Choice(
        var finish_reason: String?,
        var index: Int?,
        var message: Message?
    )

    data class Message(
        var content: String?,
        var role: String?
    )

    data class Usage(
        var completion_tokens: Int?,
        var prompt_tokens: Int?,
        var total_tokens: Int?
    )
}