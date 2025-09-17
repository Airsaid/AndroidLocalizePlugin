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
import com.airsaid.localization.translate.lang.Lang
import com.airsaid.localization.translate.util.AgentUtil
import com.airsaid.localization.translate.util.GsonUtil
import com.airsaid.localization.translate.util.UrlBuilder
import com.google.auto.service.AutoService
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.util.Pair
import com.intellij.util.io.RequestBuilder

/**
 * @author airsaid
 */
@AutoService(AbstractTranslator::class)
class GoogleTranslator : AbsGoogleTranslator() {

    companion object {
        private val LOG = Logger.getInstance(GoogleTranslator::class.java)
        const val KEY = "Google"
        const val HOST_URL = "https://translate.googleapis.com"
        private const val BASE_URL = "$HOST_URL/translate_a/single"
    }

    override val key: String = KEY

    override val name: String = "Google"

    override val isNeedAppId: Boolean = false

    override val isNeedAppKey: Boolean = false

    override fun getRequestUrl(fromLang: Lang, toLang: Lang, text: String): String {
        return UrlBuilder(BASE_URL)
            .addQueryParameter("sl", fromLang.translationCode) // source language code (auto for auto detection)
            .addQueryParameter("tl", toLang.translationCode) // translation language
            .addQueryParameter("client", "gtx") // client of request (guess)
            .addQueryParameters("dt", "t") // specify what to return
            .addQueryParameter("dj", "1") // json response with names
            .addQueryParameter("ie", "UTF-8") // input encoding
            .addQueryParameter("oe", "UTF-8") // output encoding
            .addQueryParameter("tk", GoogleToken.getToken(text)) // translate token
            .build()
    }

    override fun getRequestParams(fromLang: Lang, toLang: Lang, text: String): List<Pair<String, String>> {
        return listOf(Pair.create("q", text))
    }

    override fun configureRequestBuilder(requestBuilder: RequestBuilder) {
        requestBuilder.userAgent(AgentUtil.getUserAgent())
            .tuner { connection -> connection.setRequestProperty("Referer", GoogleTranslator.HOST_URL) }
    }

    override fun parsingResult(fromLang: Lang, toLang: Lang, text: String, resultText: String): String {
        LOG.info("parsingResult: $resultText")
        val googleTranslationResult = GsonUtil.getInstance().gson.fromJson(resultText, GoogleTranslationResult::class.java)
        return googleTranslationResult.translationResult
    }
}