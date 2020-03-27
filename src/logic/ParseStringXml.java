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

package logic;

import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.psi.PsiFile;
import com.intellij.psi.xml.*;
import module.AndroidString;
import module.Content;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author airsaid
 */
public class ParseStringXml {

    /**
     * Parse the strings.xml corresponding to {@link PsiFile}.
     *
     * @param psiFile strings.xml psi file.
     * @return parsed android string list.
     */
    @NotNull
    public static List<AndroidString> parse(@NotNull PsiFile psiFile) {
        return parse(null, psiFile);
    }

    /**
     * Parse the strings.xml corresponding to {@link PsiFile}.
     *
     * @param progressIndicator prompt text.
     * @param psiFile           strings.xml psi file.
     * @return parsed android string list.
     */
    @NotNull
    public static List<AndroidString> parse(@Nullable ProgressIndicator progressIndicator, @NotNull PsiFile psiFile) {
        List<AndroidString> androidStrings = new ArrayList<>();
        XmlFile file = (XmlFile) psiFile;
        XmlDocument document = file.getDocument();
        if (document != null) {
            XmlTag rootTag = document.getRootTag();
            if (rootTag != null) {
                XmlTag[] stringTags = rootTag.findSubTags("string");
                for (XmlTag stringTag : stringTags) {
                    if (progressIndicator != null && progressIndicator.isCanceled()) {
                        break;
                    }

                    String name = stringTag.getAttributeValue("name");
                    String translatableStr = stringTag.getAttributeValue("translatable");
                    boolean translatable = Boolean.valueOf(translatableStr == null ? "true" : translatableStr);

                    List<Content> contents = new ArrayList<>();
                    XmlTagChild[] tags = stringTag.getValue().getChildren();
                    for (XmlTagChild child : tags) {
                        if (child instanceof XmlText) {
                            XmlText xmlText = (XmlText) child;
                            contents.add(new Content(xmlText.getValue()));
                        } else if (child instanceof XmlTag) {
                            XmlTag xmlTag = (XmlTag) child;
                            if (!xmlTag.getName().equals("xliff:g")) continue;

                            String text = xmlTag.getValue().getText();
                            String id = xmlTag.getAttributeValue("id");
                            String example = xmlTag.getAttributeValue("example");
                            contents.add(new Content(text, id, example, true));
                        }
                    }

                    androidStrings.add(new AndroidString(name, contents, translatable));

                    if (progressIndicator != null) {
                        progressIndicator.setText("Loading " + name + " text from strings.xml...");
                    }
                }
            }
        }
        return androidStrings;
    }

}
