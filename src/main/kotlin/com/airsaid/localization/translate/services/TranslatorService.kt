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

package com.airsaid.localization.translate.services

import com.airsaid.localization.translate.AbstractTranslator
import com.airsaid.localization.translate.interceptors.EscapeCharactersInterceptor
import com.airsaid.localization.translate.lang.Lang
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.Logger
import org.apache.commons.lang.StringUtils
import java.util.*
import java.util.function.Consumer
import kotlin.jvm.Volatile

/**
 * @author airsaid
 */
@Service
class TranslatorService {

    interface TranslationInterceptor {
        fun process(text: String?): String?
    }

    private val defaultTranslator: AbstractTranslator
    private val cacheService: TranslationCacheService
    private val translators: Map<String, AbstractTranslator>
    private val translationInterceptors: MutableList<TranslationInterceptor>
    private var isEnableCache = true
    private var intervalTime = 0
    @Volatile
    private var selectedTranslator: AbstractTranslator
    var maxCacheSize: Int = 1000
        set(value) {
            field = value
            cacheService.setMaxCacheSize(value)
        }
    var translationInterval: Int = 0
        set(value) {
            field = value
            intervalTime = value
        }

    init {
        val translatorsMap = linkedMapOf<String, AbstractTranslator>()
        val serviceLoader = ServiceLoader.load(
            AbstractTranslator::class.java, javaClass.classLoader
        )
        for (translator in serviceLoader) {
            translatorsMap[translator.key] = translator
        }
        if (translatorsMap.isEmpty()) {
            LOG.error("No translators were registered. Translation functionality will be unavailable.")
            throw IllegalStateException("No translators registered")
        }
        translators = translatorsMap

        defaultTranslator = selectDefaultTranslator(translatorsMap)
        selectedTranslator = defaultTranslator

        cacheService = TranslationCacheService.getInstance()

        translationInterceptors = mutableListOf()
        translationInterceptors.add(EscapeCharactersInterceptor())
    }

    fun getDefaultTranslator(): AbstractTranslator = defaultTranslator

    fun getTranslators(): Map<String, AbstractTranslator> = translators

    fun setSelectedTranslator(selectedTranslator: AbstractTranslator) {
        if (this.selectedTranslator != selectedTranslator) {
            LOG.info("setTranslator: $selectedTranslator")
            this.selectedTranslator = selectedTranslator
        }
    }

    fun getSelectedTranslator(): AbstractTranslator = selectedTranslator

    fun doTranslateByAsync(fromLang: Lang, toLang: Lang, text: String, consumer: Consumer<String>) {
        ApplicationManager.getApplication().executeOnPooledThread {
            val translatedText = doTranslate(fromLang, toLang, text)
            ApplicationManager.getApplication().invokeLater {
                consumer.accept(translatedText)
            }
        }
    }

    fun doTranslate(fromLang: Lang, toLang: Lang, text: String): String {
        LOG.info("doTranslate fromLang: $fromLang, toLang: $toLang, text: $text")

        if (isEnableCache) {
            val cacheResult = cacheService.get(getCacheKey(fromLang, toLang, text))
            if (cacheResult.isNotEmpty()) {
                LOG.info("doTranslate cache result: $cacheResult")
                return cacheResult
            }
        }

        // Arabic numbers skip translation
        if (StringUtils.isNumeric(text)) {
            return text
        }

        val translator = selectedTranslator
        var result = translator.doTranslate(fromLang, toLang, text)
        LOG.info("doTranslate result: $result")
        for (interceptor in translationInterceptors) {
            result = interceptor.process(result) ?: result
            LOG.info("doTranslate interceptor process result: $result")
        }
        cacheService.put(getCacheKey(fromLang, toLang, text), result)
        delay(intervalTime)
        return result
    }

    fun setEnableCache(isEnableCache: Boolean) {
        this.isEnableCache = isEnableCache
    }

    fun isEnableCache(): Boolean = isEnableCache


    private fun getCacheKey(fromLang: Lang, toLang: Lang, text: String): String {
        return "${fromLang.code}_${toLang.code}_$text"
    }

    private fun delay(milliseconds: Int) {
        if (milliseconds <= 0) return
        try {
            LOG.info("doTranslate delay time: ${milliseconds} ms.")
            Thread.sleep(milliseconds.toLong())
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    companion object {
        private val LOG = Logger.getInstance(TranslatorService::class.java)

        fun getInstance(): TranslatorService = service()

        internal fun selectDefaultTranslator(translators: Map<String, AbstractTranslator>): AbstractTranslator {
            val preferred = translators["Microsoft"] ?: translators.values.first()
            LOG.info("Selected ${preferred.key} as default translator.")
            return preferred
        }
    }
}
