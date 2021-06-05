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

package com.airsaid.localization.constant;

/**
 * Constant Store.
 *
 * @author airsaid
 */
public interface Constants {

  String PLUGIN_NAME = "AndroidLocalize";

  String PLUGIN_ID = "com.github.airsaid.androidlocalize";

  String KEY_SELECTED_LANGUAGES = PLUGIN_ID.concat(".selected_languages");

  String KEY_IS_OVERWRITE_EXISTING_STRING = PLUGIN_ID.concat(".is_overwrite_existing_string");

  String KEY_IS_SELECT_ALL = PLUGIN_ID.concat(".is_select_all");

  String KEY_IS_OPEN_TRANSLATED_FILE = PLUGIN_ID.concat(".is_open_translated_file");

}
