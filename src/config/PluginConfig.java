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

package config;

import com.intellij.ide.util.PropertiesComponent;

import static constant.Constants.PLUGIN_ID;

/**
 * @author airsaid
 */
public final class PluginConfig {

    private static final String KEY_ENABLE_PROXY = PLUGIN_ID.concat(".enableProxy");

    private static final String KEY_HOST_NAME = PLUGIN_ID.concat(".hostName");

    private static final String KEY_PORT_NUMBER = PLUGIN_ID.concat(".portNumber");

    public static boolean isEnableProxy() {
        return PropertiesComponent.getInstance().getBoolean(KEY_ENABLE_PROXY, false);
    }

    public static void setEnableProxy(boolean enable) {
        PropertiesComponent.getInstance().setValue(KEY_ENABLE_PROXY, enable);
    }

    public static String getHostName() {
        return PropertiesComponent.getInstance().getValue(KEY_HOST_NAME, "");
    }

    public static void setHostName(String hostName) {
        PropertiesComponent.getInstance().setValue(KEY_HOST_NAME, hostName, "");
    }

    public static int getPortNumber() {
        return PropertiesComponent.getInstance().getInt(KEY_PORT_NUMBER, 0);
    }

    public static void setPortNumber(int port) {
        PropertiesComponent.getInstance().setValue(KEY_PORT_NUMBER, port, 0);
    }

}
