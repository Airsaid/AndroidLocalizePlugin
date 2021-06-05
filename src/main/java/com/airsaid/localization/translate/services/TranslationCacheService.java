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

package com.airsaid.localization.translate.services;

import com.airsaid.localization.translate.util.GsonUtil;
import com.airsaid.localization.translate.util.LRUCache;
import com.google.gson.reflect.TypeToken;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.components.*;
import com.intellij.util.xmlb.Converter;
import com.intellij.util.xmlb.XmlSerializerUtil;
import com.intellij.util.xmlb.annotations.OptionTag;
import com.intellij.util.xmlb.annotations.Transient;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Cache the translated text to local disk.
 * <p>
 * The maximum number of caches is set by the {@link #setMaxCacheSize(int)} method,
 * if exceed this size, remove old data through the LRU algorithm.
 *
 * @author airsaid
 */
@State(
    name = "com.airsaid.localization.translate.services.TranslationCacheService",
    storages = {@Storage("androidLocalizeTranslationCaches.xml")}
)
@Service
public final class TranslationCacheService implements PersistentStateComponent<TranslationCacheService>, Disposable {

  @Transient
  private static final int CACHE_MAX_SIZE = 500;

  @OptionTag(converter = LruCacheConverter.class)
  private final LRUCache<String, String> lruCache = new LRUCache<>(CACHE_MAX_SIZE);

  public static TranslationCacheService getInstance() {
    return ServiceManager.getService(TranslationCacheService.class);
  }

  public void put(@NotNull String key, @NotNull String value) {
    lruCache.put(key, value);
  }

  @NotNull
  public String get(String key) {
    String value = lruCache.get(key);
    return value != null ? value : "";
  }

  public void setMaxCacheSize(int maxCacheSize) {
    lruCache.setMaxCapacity(maxCacheSize);
  }

  @Override
  public @NotNull TranslationCacheService getState() {
    return this;
  }

  @Override
  public void loadState(@NotNull TranslationCacheService state) {
    XmlSerializerUtil.copyBean(state, this);
  }

  @Override
  public void dispose() {
    lruCache.clear();
  }

  static class LruCacheConverter extends Converter<LRUCache<String, String>> {
    @Override
    public @Nullable LRUCache<String, String> fromString(@NotNull String value) {
      Type type = new TypeToken<Map<String, String>>() {}.getType();
      Map<String, String> map = GsonUtil.getInstance().getGson().fromJson(value, type);
      LRUCache<String, String> lruCache = new LRUCache<>(CACHE_MAX_SIZE);
      for (Map.Entry<String, String> entry : map.entrySet()) {
        lruCache.put(entry.getKey(), entry.getValue());
      }
      return lruCache;
    }

    @Override
    public @Nullable String toString(@NotNull LRUCache<String, String> lruCache) {
      Map<String, String> values = new LinkedHashMap<>();
      lruCache.forEach(values::put);
      return GsonUtil.getInstance().getGson().toJson(values);
    }
  }
}
