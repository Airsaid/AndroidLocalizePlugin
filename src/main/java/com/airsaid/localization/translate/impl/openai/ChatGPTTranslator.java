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

package com.airsaid.localization.translate.impl.openai;

import com.airsaid.localization.translate.AbstractTranslator;
import com.airsaid.localization.translate.lang.Lang;
import com.airsaid.localization.translate.lang.Languages;
import com.airsaid.localization.translate.util.GsonUtil;
import com.google.auto.service.AutoService;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.util.io.RequestBuilder;
import icons.PluginIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.List;


@AutoService(AbstractTranslator.class)
public class ChatGPTTranslator extends AbstractTranslator {

    private static final Logger LOG = Logger.getInstance(ChatGPTTranslator.class);
    private static final String KEY = "ChatGPT";

    @Override
    public @NotNull String getKey() {
        return KEY;
    }

    @Override
    public @NotNull String getName() {
        return "OpenAI ChatGPT";
    }

    @Override
    public @Nullable Icon getIcon() {
        return PluginIcons.OPENAI_ICON;
    }

    @Override
    public boolean isNeedAppId() {
        return false;
    }

    @Override
    public boolean isNeedAppKey() {
        return true;
    }

    @Override
    public @NotNull List<Lang> getSupportedLanguages() {
        return Languages.getLanguages();
    }

    @Override
    public String getAppKeyDisplay() {
        return "KEY";
    }


    @Override
    public @NotNull String getRequestUrl(@NotNull Lang fromLang, @NotNull Lang toLang, @NotNull String text) {
        return "https://api.openai.com/v1/chat/completions";
    }

    @Override
    @NotNull
    public String getRequestBody(@NotNull Lang fromLang, @NotNull Lang toLang, @NotNull String text) {
        ChatGPTMessage msg = new ChatGPTMessage("user", String.format("Translate this %s text into %s", fromLang.getEnglishName(), toLang.getEnglishName()));
        OpenAIRequest body = new OpenAIRequest("gpt-3.5-turbo", List.of(msg));

        return GsonUtil.getInstance().getGson().toJson(body);
    }

    @Override
    public void configureRequestBuilder(@NotNull RequestBuilder requestBuilder) {
        requestBuilder.tuner(connection -> {
            connection.setRequestProperty("Authorization", "Bearer " + getAppKey());
            connection.setRequestProperty("Content-Type", "application/json");
        });
    }

    @Override
    public @NotNull String parsingResult(@NotNull Lang fromLang, @NotNull Lang toLang, @NotNull String text, @NotNull String resultText) {
        LOG.info("parsingResult: " + resultText);
        return GsonUtil.getInstance().getGson().fromJson(resultText, OpenAIResponse.class).getTranslation();
    }

}
