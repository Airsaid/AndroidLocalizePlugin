/*
 * Copyright 2018 Airsaid. https://github.com/airsaid
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
 */

package icons;

import com.intellij.openapi.util.IconLoader;

import javax.swing.*;

/**
 * @author airsaid
 */
public interface PluginIcons {
  Icon TRANSLATE_ACTION_ICON = load("/icons/icon_translate.svg");
  Icon GOOGLE_ICON = load("/icons/icon_google.svg");
  Icon BAIDU_ICON = load("/icons/icon_baidu.svg");
  Icon YOUDAO_ICON = load("/icons/icon_youdao.svg");
  Icon MICROSOFT_ICON = load("/icons/icon_microsoft.svg");
  Icon ALI_ICON = load("/icons/icon_ali.svg");
  Icon DEEP_L_ICON = load("/icons/icon_deepl.svg");
  Icon OPENAI_ICON = load("/icons/icon_openai.svg");

  private static Icon load(String path) {
    return IconLoader.getIcon(path, PluginIcons.class);
  }
}