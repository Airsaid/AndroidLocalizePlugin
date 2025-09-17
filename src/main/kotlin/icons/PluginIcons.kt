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

package icons

import com.intellij.openapi.util.IconLoader
import javax.swing.Icon

/**
 * @author airsaid
 */
object PluginIcons {
    @JvmField
    val TRANSLATE_ACTION_ICON: Icon = load("/icons/icon_translate.svg")

    @JvmField
    val GOOGLE_ICON: Icon = load("/icons/icon_google.svg")

    @JvmField
    val BAIDU_ICON: Icon = load("/icons/icon_baidu.svg")

    @JvmField
    val YOUDAO_ICON: Icon = load("/icons/icon_youdao.svg")

    @JvmField
    val MICROSOFT_ICON: Icon = load("/icons/icon_microsoft.svg")

    @JvmField
    val ALI_ICON: Icon = load("/icons/icon_ali.svg")

    @JvmField
    val DEEP_L_ICON: Icon = load("/icons/icon_deepl.svg")

    @JvmField
    val OPENAI_ICON: Icon = load("/icons/icon_openai.svg")

    private fun load(path: String): Icon {
        return IconLoader.getIcon(path, PluginIcons::class.java)
    }
}