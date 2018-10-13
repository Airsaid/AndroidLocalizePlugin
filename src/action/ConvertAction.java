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

package action;


import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import logic.LanguageHelper;
import module.AndroidString;
import org.jetbrains.annotations.NotNull;
import task.GetAndroidStringTask;
import task.TranslateTask;
import translate.lang.LANG;
import ui.SelectLanguageDialog;

import java.util.List;

/**
 * @author airsaid
 */
public class ConvertAction extends AnAction implements SelectLanguageDialog.OnClickListener {

    private Project             mProject;
    private VirtualFile         mSelectFile;
    private List<AndroidString> mAndroidStrings;

    @Override
    public void actionPerformed(AnActionEvent e) {
        mProject = e.getData(CommonDataKeys.PROJECT);
        PsiFile file = e.getData(CommonDataKeys.PSI_FILE);
        GetAndroidStringTask getAndroidStringTask = new GetAndroidStringTask(mProject, "Load strings.xml...", file);
        getAndroidStringTask.setOnGetAndroidStringListener(new GetAndroidStringTask.OnGetAndroidStringListener() {
            @Override
            public void onGetSuccess(@NotNull List<AndroidString> list) {
                if (!isTranslatable(list)) {
                    Messages.showInfoMessage("strings.xml has no text to translate!", "Prompt");
                    return;
                }
                mAndroidStrings = list;
                showSelectLanguageDialog();
            }

            @Override
            public void onGetError(@NotNull Throwable error) {
                Messages.showErrorDialog("Load strings.xml error: " + error, "Error");
            }
        });
        getAndroidStringTask.queue();
    }

    private void showSelectLanguageDialog() {
        SelectLanguageDialog dialog = new SelectLanguageDialog(mProject);
        dialog.setOnClickListener(this);
        dialog.show();
    }

    @Override
    public void update(AnActionEvent e) {
        super.update(e);
        // The translation option is only show when strings.xml is selected.
        mSelectFile = e.getData(CommonDataKeys.VIRTUAL_FILE);
        boolean isSelectStringsFile = isSelectStringsFile(mSelectFile);
        e.getPresentation().setEnabledAndVisible(isSelectStringsFile);
    }

    /**
     * Verify that the selected file is a strings.xml file.
     *
     * @param file selected file
     * @return true: indicating that the selected file is the strings.xml file.
     */
    private boolean isSelectStringsFile(VirtualFile file) {
        if (file == null) return false;

        VirtualFile parent = file.getParent();
        if (parent == null) return false;

        String parentName = parent.getName();
        if (!"values".equals(parentName)) return false;

        return "strings.xml".equals(file.getName());
    }

    /**
     * Verify that there is a text in the strings.xml file that needs to be translated.
     *
     * @param list strings.xml text list.
     * @return true: there is text that needs to be translated.
     */
    private boolean isTranslatable(@NotNull List<AndroidString> list) {
        boolean isTranslatable = false;
        for (AndroidString androidString : list) {
            if (androidString.isTranslatable()) {
                isTranslatable = true;
                break;
            }
        }
        return isTranslatable;
    }

    @Override
    public void onClickListener(List<LANG> selectedLanguage) {
        LanguageHelper.saveSelectedLanguage(mProject, selectedLanguage);
        TranslateTask translationTask = new TranslateTask(
                mProject, "In translation...", selectedLanguage, mAndroidStrings, mSelectFile);
        translationTask.setOnTranslateListener(new TranslateTask.OnTranslateListener() {
            @Override
            public void onTranslateSuccess() {}

            @Override
            public void onTranslateError(Throwable e) {
                Messages.showErrorDialog("Translate error: " + e, "Error");
            }
        });
        translationTask.queue();
    }

}
