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

package com.airsaid.localization.translate.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * @author airsaid
 */
public class GsonUtil {

  private final Gson gson;

  public GsonUtil() {
    gson = new GsonBuilder().create();
  }

  public static GsonUtil getInstance() {
    return GsonUtilHolder.sInstance;
  }

  public Gson getGson() {
    return gson;
  }

  private static class GsonUtilHolder {
    private static final GsonUtil sInstance = new GsonUtil();
  }

}
