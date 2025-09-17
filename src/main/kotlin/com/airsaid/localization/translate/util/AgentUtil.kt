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

package com.airsaid.localization.translate.util

import com.intellij.openapi.util.SystemInfo

/**
 * @author airsaid
 */
object AgentUtil {

    private const val CHROME_VERSION = "98.0.4758.102"
    private const val EDGE_VERSION = "98.0.1108.62"

    fun getUserAgent(): String {
        val arch = System.getProperty("os.arch")
        val is64Bit = arch?.contains("64") == true
        val systemInformation = when {
            SystemInfo.isWindows -> {
                if (is64Bit) "Windows NT ${SystemInfo.OS_VERSION}; Win64; x64" else "Windows NT ${SystemInfo.OS_VERSION}"
            }
            SystemInfo.isMac -> {
                val parts = SystemInfo.OS_VERSION.split(".").toMutableList()
                if (parts.size < 3) {
                    parts.add("0")
                }
                "Macintosh; Intel Mac OS X ${parts.joinToString("_")}"
            }
            else -> {
                if (is64Bit) "X11; Linux x86_64" else "X11; Linux x86"
            }
        }
        return "Mozilla/5.0 ($systemInformation) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/$CHROME_VERSION Safari/537.36 Edg/$EDGE_VERSION"
    }
}