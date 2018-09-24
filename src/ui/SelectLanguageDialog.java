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

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.ui.components.JBCheckBox;
import translate.lang.LANG;
import org.jetbrains.annotations.Nullable;
import translate.trans.impl.GoogleTranslator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Select the language dialog you want to convert.
 *
 * @author airsaid
 */
public class SelectLanguageDialog extends DialogWrapper {

    private OnClickListener mOnClickListener;
    private List<LANG> mSelectLanguages = new ArrayList<>();

    public interface OnClickListener {
        void onClickListener(List<LANG> selectLanguage);
    }

    public SelectLanguageDialog(@Nullable Project project) {
        super(project, false);
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
        // add select all
        final JBCheckBox selectAll = new JBCheckBox("Select All");
        panel.add(selectAll, BorderLayout.NORTH);
        selectAll.addItemListener(e -> {
            int state = e.getStateChange();
            if (state == ItemEvent.SELECTED) {
                selectAll(container, true);
            } else {
                selectAll(container, false);
            }
        });
        // add language
        mSelectLanguages.clear();
        List<LANG> supportLanguages = new GoogleTranslator().getSupportLang();
        container.setLayout(new GridLayout(supportLanguages.size() / 4, 4));
        for (LANG language : supportLanguages) {
            JBCheckBox checkBoxLanguage = new JBCheckBox();
            checkBoxLanguage.setText(language.getEnglishName()
                    .concat("(").concat(language.getCode()).concat(")"));
            container.add(checkBoxLanguage);
            checkBoxLanguage.addItemListener(e -> {
                int state = e.getStateChange();
                if (state == ItemEvent.SELECTED) {
                    mSelectLanguages.add(language);
                } else {
                    mSelectLanguages.remove(language);
                }
            });
        }
        panel.add(container, BorderLayout.CENTER);
        return panel;
    }

    public void setOnClickListener(OnClickListener listener) {
        mOnClickListener = listener;
    }

    private void selectAll(Container container, boolean selectAll) {
        for (Component component : container.getComponents()) {
            if (component instanceof JBCheckBox) {
                JBCheckBox checkBox = (JBCheckBox) component;
                checkBox.setSelected(selectAll);
            }
        }
    }
}
