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

package com.airsaid.localization.translate.impl.deepl;

import com.airsaid.localization.translate.AbstractTranslator;
import com.airsaid.localization.translate.lang.Lang;
import com.google.auto.service.AutoService;
import com.intellij.util.io.RequestBuilder;
import org.jetbrains.annotations.NotNull;

/**
 * @author airsaid
 */
@AutoService(AbstractTranslator.class)
public class DeepLXOfficialTranslator extends DeepLXTranslator {

    private static final String KEY = "DeepLXOfficial";
    private static final String HOST_URL = "https://127.0.0.1:1188/v2";
    private static final String TRANSLATE_URL = HOST_URL.concat("/translate");

    @Override
    public @NotNull String getKey() {
        return KEY;
    }

    @Override
    public @NotNull String getName() {
        return KEY;
    }

    @Override
    public @NotNull String getRequestUrl(@NotNull Lang fromLang, @NotNull Lang toLang, @NotNull String text) {
        return TRANSLATE_URL;
    }

    @Override
    public void configureRequestBuilder(@NotNull RequestBuilder requestBuilder) {
        requestBuilder.tuner(connection -> {
            connection.setRequestProperty("Authorization", "DeepL-Auth-Key " + getAppKey());
            connection.setRequestProperty("Content-Type", "application/json");
        });
    }

    @Override
    public boolean isNeedAppKey() {
        return true;
    }
}
