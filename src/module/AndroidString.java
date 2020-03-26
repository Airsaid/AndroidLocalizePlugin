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

import java.util.ArrayList;
import java.util.List;

/**
 * @author airsaid
 */
public class AndroidString implements Cloneable {

    private String        name;
    private List<Content> contents;
    private boolean       translatable;

    public AndroidString(String name, List<Content> contents, boolean translatable) {
        this.name = name;
        this.contents = contents;
        this.translatable = translatable;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Content> getContents() {
        return contents;
    }

    public void setContents(List<Content> contents) {
        this.contents = contents;
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
    public AndroidString clone() {
        try {
            AndroidString clone = (AndroidString) super.clone();
            List<Content> contents = clone.getContents();
            List<Content> cloneContents = new ArrayList<>(contents.size());
            for (Content content : clone.getContents()) {
                cloneContents.add(content.clone());
            }
            clone.setContents(cloneContents);
            return clone;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String toString() {
        return "AndroidString{" +
                "name='" + name + '\'' +
                ", contents=" + contents +
                ", translatable=" + translatable +
                '}';
    }
}
