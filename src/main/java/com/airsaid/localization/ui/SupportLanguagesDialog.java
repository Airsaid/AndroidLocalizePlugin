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

package com.airsaid.localization.ui;

import com.airsaid.localization.translate.AbstractTranslator;
import com.airsaid.localization.translate.lang.Lang;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBLabel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.Comparator;
import java.util.List;

/**
 * @author airsaid
 */
public class SupportLanguagesDialog extends DialogWrapper {

  private final AbstractTranslator mTranslator;

  public SupportLanguagesDialog(AbstractTranslator translator) {
    super(true);
    setTitle(translator.getName() + " Translator Supported Languages");
    mTranslator = translator;
    init();
  }

  @Override
  protected @Nullable JComponent createCenterPanel() {
    List<Lang> supportedLanguages = mTranslator.getSupportedLanguages();
    supportedLanguages.sort(new EnglishNameComparator());
    JPanel contentPanel = new JPanel(new GridLayout(supportedLanguages.size() / 4, 4, 10, 20));
    for (Lang supportedLanguage : supportedLanguages) {
      contentPanel.add(new JBLabel(supportedLanguage.getEnglishName()));
    }
    return contentPanel;
  }

  @Override
  protected @Nullable String getDimensionServiceKey() {
    String key = mTranslator.getKey();
    return "#com.airsaid.localization.ui.SupportLanguagesDialog#".concat(key);
  }

  @Override
  protected Action @NotNull [] createActions() {
    return new Action[]{};
  }

  static class EnglishNameComparator implements Comparator<Lang> {
    @Override
    public int compare(Lang o1, Lang o2) {
      return o1.getEnglishName().compareTo(o2.getEnglishName());
    }
  }
}
