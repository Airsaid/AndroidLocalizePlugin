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

package com.airsaid.localization.translate.impl.groq;

import com.airsaid.localization.translate.AbstractTranslator;
import com.airsaid.localization.translate.impl.openai.ChatGPTMessage;
import com.airsaid.localization.translate.impl.openai.OpenAIRequest;
import com.airsaid.localization.translate.impl.openai.OpenAIResponse;
import com.airsaid.localization.translate.lang.Lang;
import com.airsaid.localization.translate.lang.Languages;
import com.airsaid.localization.translate.util.GsonUtil;
import com.google.auto.service.AutoService;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.util.io.RequestBuilder;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

import javax.swing.Icon;

import icons.PluginIcons;


@AutoService(AbstractTranslator.class)
public class GroqTranslator extends AbstractTranslator {

    private static final Logger LOG = Logger.getInstance(GroqTranslator.class);
    private static final String KEY = "Groq";

    @Override
    public @NotNull String getKey() {
        return KEY;
    }

    @Override
    public @NotNull String getName() {
        return "Groq";
    }

    @Override
    public @Nullable Icon getIcon() {
        return PluginIcons.GROQ_ICON;
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
        return "https://api.groq.com/openai/v1/chat/completions";
    }

    @Override
    @NotNull
    public String getRequestBody(@NotNull Lang fromLang, @NotNull Lang toLang, @NotNull String text) {
        StringBuilder roleSystem = new StringBuilder();
        Arrays.asList(
                "You are a translator.",
                "Please translate directly without explanation.",
                "Please translate the text into a colloquial, professional, elegant and fluent content,without the style of machine translation.",
                "Please do not return the original text, only return the translated content.",
                "Translated content can be used in Android app UI (limited space, concise translations!).",
                String.format("Translate the following text from %s to %s without the style of machine translation.", fromLang.getEnglishName(), toLang.getEnglishName())
        ).forEach(roleSystem::append);

        ChatGPTMessage role = new ChatGPTMessage("system", roleSystem.toString());
        ChatGPTMessage msg = new ChatGPTMessage("user", text);

        OpenAIRequest body = new OpenAIRequest("mixtral-8x7b-32768", List.of(role, msg));

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
        LOG.info("parsingResult Groq: " + resultText);
        return GsonUtil.getInstance().getGson().fromJson(resultText, OpenAIResponse.class).getTranslation();
    }

}
