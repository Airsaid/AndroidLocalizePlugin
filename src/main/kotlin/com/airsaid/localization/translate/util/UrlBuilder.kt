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

package com.airsaid.localization.translate.util

/**
 * @author airsaid
 */
class UrlBuilder(private val baseUrl: String) {

    private val queryParameters: MutableList<Pair<String, String>> = mutableListOf()

    fun addQueryParameter(key: String, value: String): UrlBuilder {
        queryParameters.add(Pair(key, value))
        return this
    }

    fun addQueryParameters(key: String, vararg values: String): UrlBuilder {
        queryParameters.addAll(values.map { value -> Pair(key, value) })
        return this
    }

    fun build(): String {
        val result = StringBuilder(baseUrl)
        for (i in queryParameters.indices) {
            if (i == 0) {
                result.append("?")
            } else {
                result.append("&")
            }
            val param = queryParameters[i]
            val key = param.first
            val value = param.second
            result.append(key)
                .append("=")
                .append(value)
        }
        return result.toString()
    }
}