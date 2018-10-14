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

package ui;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.ui.components.JBCheckBox;
import constant.Constants;
import logic.LanguageHelper;
import org.jetbrains.annotations.Nullable;
import translate.lang.LANG;
import translate.trans.impl.GoogleTranslator;

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

    private Project mProject;
    private OnClickListener mOnClickListener;
    private List<LANG> mSelectLanguages = new ArrayList<>();

    public interface OnClickListener {
        void onClickListener(List<LANG> selectedLanguage);
    }

    public SelectLanguageDialog(@Nullable Project project) {
        super(project, false);
        this.mProject = project;
        setTitle("Select Convert Languages");
        setResizable(true);
        init();
    }

    @Override
    protected void doOKAction() {
        if (mSelectLanguages.size() <= 0) {
            Messages.showErrorDialog("Please select the language you need to translate!", "Error");
            return;
        }
        if (mOnClickListener != null) {
            mOnClickListener.onClickListener(mSelectLanguages);
        }
        super.doOKAction();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return doCreateCenterPanel();
    }

    private JComponent doCreateCenterPanel() {
        final JPanel panel = new JPanel(new BorderLayout(16, 6));
        final Container container = new Container();
        // add overwrite existing string
        final JBCheckBox overwriteExistingString = new JBCheckBox("Overwrite Existing String");
        panel.add(overwriteExistingString, BorderLayout.NORTH);
        overwriteExistingString.addItemListener(e -> {
            int state = e.getStateChange();
            PropertiesComponent.getInstance(mProject)
                    .setValue(Constants.KEY_IS_OVERWRITE_EXISTING_STRING, state == ItemEvent.SELECTED);
        });
        boolean isOverwriteExistingString = PropertiesComponent.getInstance(mProject)
                .getBoolean(Constants.KEY_IS_OVERWRITE_EXISTING_STRING);
        overwriteExistingString.setSelected(isOverwriteExistingString);
        // add language
        mSelectLanguages.clear();
        List<LANG> supportLanguages = new GoogleTranslator().getSupportLang();
        List<String> selectedLanguageCodes = LanguageHelper.getSelectedLanguageCodes(mProject);
        // sort by country code, easy to find
        supportLanguages.sort(new CountryCodeComparator());
        container.setLayout(new GridLayout(supportLanguages.size() / 4, 4));
        for (LANG language : supportLanguages) {
            String code = language.getCode();
            JBCheckBox checkBoxLanguage = new JBCheckBox();
            checkBoxLanguage.setText(language.getEnglishName()
                    .concat("(").concat(code).concat(")"));
            container.add(checkBoxLanguage);
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
        panel.add(container, BorderLayout.CENTER);
        return panel;
    }

    public void setOnClickListener(OnClickListener listener) {
        mOnClickListener = listener;
    }

    class CountryCodeComparator implements Comparator<LANG> {
        @Override
        public int compare(LANG o1, LANG o2) {
            return o1.getCode().compareTo(o2.getCode());
        }
    }
}
