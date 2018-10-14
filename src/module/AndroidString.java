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

package module;

/**
 * @author airsaid
 */
public class AndroidString {

    private String name;
    private String value;
    private boolean translatable;

    public AndroidString(String name, String value, boolean translatable) {
        this.name = name;
        this.value = value;
        this.translatable = translatable;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isTranslatable() {
        return translatable;
    }

    public void setTranslatable(boolean translatable) {
        this.translatable = translatable;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof AndroidString)) {
            return false;
        }
        AndroidString androidString = (AndroidString) obj;
        return name.equals(androidString.name);
    }

    @Override
    public String toString() {
        return "AndroidString{" +
                "name='" + name + '\'' +
                ", value='" + value + '\'' +
                ", translatable=" + translatable +
                '}';
    }
}
