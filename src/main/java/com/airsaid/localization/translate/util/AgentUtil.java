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

import com.intellij.openapi.util.SystemInfo;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author airsaid
 */
public class AgentUtil {

  private static final String CHROME_VERSION = "98.0.4758.102";
  private static final String EDGE_VERSION = "98.0.1108.62";

  private AgentUtil() {
    throw new AssertionError("No com.airsaid.localization.translate.util.AgentUtil instances for you!");
  }

  public static String getUserAgent() {
    String arch = System.getProperty("os.arch");
    boolean is64Bit = arch != null && arch.contains("64");
    String systemInformation;
    if (SystemInfo.isWindows) {
      systemInformation = is64Bit ? "Windows NT " + SystemInfo.OS_VERSION + "; Win64; x64" : "Windows NT " + SystemInfo.OS_VERSION;
    } else if (SystemInfo.isMac) {
      List<String> parts = Arrays.stream(SystemInfo.OS_VERSION.split("\\.")).collect(Collectors.toList());
      if (parts.size() < 3) {
        parts.add("0");
      }
      systemInformation = String.format("Macintosh; Intel Mac OS X %s", String.join("_", parts));
    } else {
      systemInformation = is64Bit ? "X11; Linux x86_64" : "X11; Linux x86";
    }
    return "Mozilla/5.0 (".concat(systemInformation).concat(") AppleWebKit/537.36 (KHTML, like Gecko) Chrome/")
        .concat(CHROME_VERSION).concat(" Safari/537.36 Edg/").concat(EDGE_VERSION);
  }

}
