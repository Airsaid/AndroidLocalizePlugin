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

import com.airsaid.localization.translate.util.GsonUtil
import com.airsaid.localization.translate.util.LRUCache
import com.google.gson.reflect.TypeToken
import com.intellij.openapi.Disposable
import com.intellij.openapi.components.*
import com.intellij.util.xmlb.Converter
import com.intellij.util.xmlb.XmlSerializerUtil
import com.intellij.util.xmlb.annotations.OptionTag
import com.intellij.util.xmlb.annotations.Transient
import java.lang.reflect.Type

/**
 * Cache the translated text to local disk.
 *
 * The maximum number of caches is set by the [setMaxCacheSize] method,
 * if exceed this size, remove old data through the LRU algorithm.
 *
 * @author airsaid
 */
@State(
    name = "com.airsaid.localization.translate.services.TranslationCacheService",
    storages = [Storage("androidLocalizeTranslationCaches.xml")]
)
@Service
class TranslationCacheService : PersistentStateComponent<TranslationCacheService>, Disposable {

    @Transient
    private val lruCache = LRUCache<String, String>(CACHE_MAX_SIZE)

    @OptionTag(converter = LruCacheConverter::class)
    fun getLruCache(): LRUCache<String, String> = lruCache

    fun put(key: String, value: String) {
        lruCache.put(key, value)
    }

    fun get(key: String?): String {
        val value = lruCache.get(key)
        return value ?: ""
    }

    fun setMaxCacheSize(maxCacheSize: Int) {
        lruCache.setMaxCapacity(maxCacheSize)
    }

    override fun getState(): TranslationCacheService = this

    override fun loadState(state: TranslationCacheService) {
        XmlSerializerUtil.copyBean(state, this)
    }

    override fun dispose() {
        lruCache.clear()
    }

    class LruCacheConverter : Converter<LRUCache<String, String>>() {
        override fun fromString(value: String): LRUCache<String, String>? {
            val type: Type = object : TypeToken<Map<String, String>>() {}.type
            val map: Map<String, String> = GsonUtil.getInstance().gson.fromJson(value, type)
            val lruCache = LRUCache<String, String>(CACHE_MAX_SIZE)
            for ((key, value1) in map) {
                lruCache.put(key, value1)
            }
            return lruCache
        }

        override fun toString(lruCache: LRUCache<String, String>): String? {
            val values = linkedMapOf<String, String>()
            lruCache.forEach { key, value -> values[key] = value }
            return GsonUtil.getInstance().gson.toJson(values)
        }
    }

    companion object {
        private const val CACHE_MAX_SIZE = 500

        fun getInstance(): TranslationCacheService = service()
    }
}