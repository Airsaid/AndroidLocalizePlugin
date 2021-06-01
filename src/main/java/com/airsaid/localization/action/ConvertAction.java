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

package com.airsaid.localization.action;

import com.airsaid.localization.module.AndroidString;
import com.airsaid.localization.services.AndroidStringsService;
import com.airsaid.localization.task.TranslateTask;
import com.airsaid.localization.translate.lang.Lang;
import com.airsaid.localization.ui.SelectLanguagesDialog;
import com.airsaid.localization.utils.NotificationUtil;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Convert strings.xml to other languages that can be used to localize your Android app.
 *
 * @author airsaid
 */
public class ConvertAction extends AnAction implements SelectLanguagesDialog.OnClickListener {

  private Project mProject;
  private PsiFile mStringsFile;
  private List<AndroidString> mAndroidStrings;
  private final AndroidStringsService mStringsService = AndroidStringsService.getInstance();

  @Override
  public void actionPerformed(AnActionEvent e) {
    mProject = e.getRequiredData(CommonDataKeys.PROJECT);
    mStringsFile = e.getRequiredData(CommonDataKeys.PSI_FILE);

    mStringsService.loadStrings(mStringsFile, androidStrings -> {
      if (!isTranslatable(androidStrings)) {
        Messages.showInfoMessage("The strings.xml has no text to translate!", "Prompt");
        return;
      }
      mAndroidStrings = androidStrings;
      showSelectLanguageDialog();
    });
  }

  // Verify that there is a text in the strings.xml file that needs to be translated.
  private boolean isTranslatable(@NotNull List<AndroidString> androidStrings) {
    boolean isTranslatable = false;
    for (AndroidString androidString : androidStrings) {
      if (androidString.isTranslatable()) {
        isTranslatable = true;
        break;
      }
    }
    return isTranslatable;
  }

  private void showSelectLanguageDialog() {
    SelectLanguagesDialog dialog = new SelectLanguagesDialog(mProject);
    dialog.setOnClickListener(this);
    dialog.show();
  }

  @Override
  public void update(@NotNull AnActionEvent e) {
    // The translation option is only show when strings.xml is selected
    Project project = e.getData(CommonDataKeys.PROJECT);
    boolean isSelectStringsFile = mStringsService.isStringsFile(e.getData(CommonDataKeys.PSI_FILE));
    e.getPresentation().setEnabledAndVisible(project != null && isSelectStringsFile);
  }

  @Override
  public void onClickListener(List<Lang> selectedLanguage) {
    TranslateTask translationTask = new TranslateTask(mProject, "Translating...", selectedLanguage, mAndroidStrings, mStringsFile);
    translationTask.setOnTranslateListener(new TranslateTask.OnTranslateListener() {
      @Override
      public void onTranslateSuccess() {
        NotificationUtil.notifyInfo(mProject, "Translation completed!");
      }

      @Override
      public void onTranslateError(Throwable e) {
        NotificationUtil.notifyWarning(mProject, "Translation error: " + e);
      }
    });
    translationTask.queue();
  }

}
