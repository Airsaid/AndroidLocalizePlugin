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

package com.airsaid.localization.translate;

import com.airsaid.localization.translate.lang.Lang;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.List;

/**
 * @author airsaid
 */
public interface TranslatorConfigurable {

  @NotNull
  String getKey();

  @NotNull
  String getName();

  @Nullable
  Icon getIcon();

  @NotNull
  List<Lang> getSupportedLanguages();

  boolean isNeedAppId();

  @Nullable
  String getAppId();

  String getAppIdDisplay();

  boolean isNeedAppKey();

  @Nullable
  String getAppKey();

  String getAppKeyDisplay();

  @Nullable
  String getApplyAppIdUrl();
}
