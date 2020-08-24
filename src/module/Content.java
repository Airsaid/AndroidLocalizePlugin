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

import java.util.Map;

/**
 * @author airsaid
 */
public class Content implements Cloneable {
    private String  text;
    private String  id;
    private String  example;
    private boolean isIgnore;
    private String tagName;
    private Map<String,String> attrs;

    public Content(String text) {
        this.text = text;
    }

    public Content(String text, String id, String example, boolean isIgnore) {
        this.text = text;
        this.id = id;
        this.example = example;
        this.isIgnore = isIgnore;
    }

    public Content(String text, String tagName, Map<String,String> attrs, boolean isIgnore) {
        this.text = text;
        this.tagName = tagName;
        if(attrs!=null&&attrs.size()>0){
            this.attrs = attrs;
        }else {
            this.attrs = null;
        }
        this.isIgnore = isIgnore;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getExample() {
        return example;
    }

    public void setExample(String example) {
        this.example = example;
    }

    public boolean isIgnore() {
        return isIgnore;
    }

    public void setIgnore(boolean ignore) {
        isIgnore = ignore;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public Map<String, String> getAttrs() {
        return attrs;
    }

    public void setAttrs(Map<String, String> attrs) {
        this.attrs = attrs;
    }

    @Override
    public Content clone() {
        try {
            return (Content) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String toString() {
        return "Content{" +
                "text='" + text + '\'' +
                ", id='" + id + '\'' +
                ", example='" + example + '\'' +
                ", isIgnore=" + isIgnore + '\'' +
                ", tagName=" + tagName +
                '}';
    }
}