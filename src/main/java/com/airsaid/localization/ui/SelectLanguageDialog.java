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

package com.airsaid.localization.ui;

import com.airsaid.localization.constant.Constants;
import com.airsaid.localization.logic.LanguageHelper;
import com.airsaid.localization.translate.lang.Lang;
import com.airsaid.localization.translate.trans.impl.GoogleTranslator;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.ui.components.JBCheckBox;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Select the language dialog you want to convert.
 *
 * @author airsaid
 */
public class SelectLanguageDialog extends DialogWrapper {
    private JPanel    myPanel;
    private JCheckBox overwriteExistingStringCheckBox;
    private JCheckBox selectAllCheckBox;
    private JPanel    languagesPanel;

    private Project         mProject;
    private OnClickListener mOnClickListener;
    private List<Lang>      mSelectLanguages = new ArrayList<>();

    public interface OnClickListener {
        void onClickListener(List<Lang> selectedLanguage);
    }

    public SelectLanguageDialog(@Nullable Project project) {
        super(project, false);
        this.mProject = project;
        doCreateCenterPanel();
        setTitle("Select Convert Languages");
        setResizable(true);
        init();
    }

    public void setOnClickListener(OnClickListener listener) {
        mOnClickListener = listener;
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return myPanel;
    }

    private void doCreateCenterPanel() {
        // add language
        mSelectLanguages.clear();
        List<Lang> supportLanguages = new GoogleTranslator().getSupportLang();
        List<String> selectedLanguageCodes = LanguageHelper.getSelectedLanguageCodes(mProject);
        // sort by country code, easy to find
        supportLanguages.sort(new CountryCodeComparator());
        languagesPanel.setLayout(new GridLayout(supportLanguages.size() / 4, 4));
        for (Lang language : supportLanguages) {
            String code = language.getCode();
            JBCheckBox checkBoxLanguage = new JBCheckBox();
            checkBoxLanguage.setText(language.getEnglishName()
                    .concat("(").concat(code).concat(")"));
            languagesPanel.add(checkBoxLanguage);
            checkBoxLanguage.addItemListener(e -> {
                int state = e.getStateChange();
                if (state == ItemEvent.SELECTED) {
                    mSelectLanguages.add(language);
                } else {
                    mSelectLanguages.remove(language);
                }
            });
            if (selectedLanguageCodes != null && selectedLanguageCodes.contains(code)) {
                checkBoxLanguage.setSelected(true);
            }
        }

        boolean isOverwriteExistingString = PropertiesComponent.getInstance(mProject)
                .getBoolean(Constants.KEY_IS_OVERWRITE_EXISTING_STRING);
        overwriteExistingStringCheckBox.setSelected(isOverwriteExistingString);
        overwriteExistingStringCheckBox.addItemListener(e -> {
            int state = e.getStateChange();
            PropertiesComponent.getInstance(mProject)
                    .setValue(Constants.KEY_IS_OVERWRITE_EXISTING_STRING, state == ItemEvent.SELECTED);
        });

        boolean isSelectAll = PropertiesComponent.getInstance(mProject)
                .getBoolean(Constants.KEY_IS_SELECT_ALL);
        selectAllCheckBox.setSelected(isSelectAll);
        selectAllCheckBox.addItemListener(e -> {
            int state = e.getStateChange();
            selectAll(state == ItemEvent.SELECTED);
            PropertiesComponent.getInstance(mProject)
                    .setValue(Constants.KEY_IS_SELECT_ALL, state == ItemEvent.SELECTED);
        });
    }

    private void selectAll(boolean selectAll) {
        for (Component component : languagesPanel.getComponents()) {
            if (component instanceof JBCheckBox) {
                JBCheckBox checkBox = (JBCheckBox) component;
                checkBox.setSelected(selectAll);
            }
        }
    }

    @Override
    protected void doOKAction() {
        LanguageHelper.saveSelectedLanguage(mProject, mSelectLanguages);
        if (mSelectLanguages.size() <= 0) {
            Messages.showErrorDialog("Please select the language you need to translate!", "Error");
            return;
        }
        if (mOnClickListener != null) {
            mOnClickListener.onClickListener(mSelectLanguages);
        }
        super.doOKAction();
    }

    class CountryCodeComparator implements Comparator<Lang> {
        @Override
        public int compare(Lang o1, Lang o2) {
            return o1.getCode().compareTo(o2.getCode());
        }
    }
}
