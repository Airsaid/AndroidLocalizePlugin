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

package com.airsaid.localization.task;

import com.airsaid.localization.constant.Constants;
import com.airsaid.localization.model.AndroidString;
import com.airsaid.localization.services.AndroidStringsService;
import com.airsaid.localization.translate.lang.Lang;
import com.airsaid.localization.translate.services.TranslatorService;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author airsaid
 */
public class TranslateTask extends Task.Backgroundable {

  private static final Logger LOG = Logger.getInstance(TranslateTask.class);

  private final List<Lang> mToLanguages;
  private final List<AndroidString> mAndroidStrings;
  private final VirtualFile mStringsFile;
  private final TranslatorService mTranslatorService;
  private final AndroidStringsService mStringsService;

  private OnTranslateListener mOnTranslateListener;

  public interface OnTranslateListener {
    void onTranslateSuccess();

    void onTranslateError(Throwable e);
  }

  public TranslateTask(@Nullable Project project, @Nls @NotNull String title, List<Lang> languages,
                       List<AndroidString> androidStrings, PsiFile stringsFile) {
    super(project, title);
    mToLanguages = languages;
    mAndroidStrings = androidStrings;
    mStringsFile = stringsFile.getVirtualFile();
    mTranslatorService = TranslatorService.getInstance();
    mStringsService = AndroidStringsService.getInstance();
  }

  /**
   * Set translate result listener.
   *
   * @param listener callback interface. success or fail.
   */
  public void setOnTranslateListener(OnTranslateListener listener) {
    mOnTranslateListener = listener;
  }

  @Override
  public void run(@NotNull ProgressIndicator progressIndicator) {
    final boolean isOverwriteExistingString = PropertiesComponent.getInstance(myProject)
        .getBoolean(Constants.KEY_IS_OVERWRITE_EXISTING_STRING);

    LOG.info("run isOverwriteExistingString: " + isOverwriteExistingString);

    for (Lang toLanguage : mToLanguages) {
      if (progressIndicator.isCanceled()) break;

      progressIndicator.setText("Translating in the " + toLanguage.getEnglishName() + " language...");

      final VirtualFile resourceDir = mStringsFile.getParent().getParent();
      final PsiFile toStringsPsiFile = mStringsService.getStringsPsiFile(myProject, resourceDir, toLanguage);
      LOG.info("Translating language: " + toLanguage.getEnglishName() + ", toStringsPsiFile: " + toStringsPsiFile);
      if (toStringsPsiFile != null) {
        List<AndroidString> toAndroidStrings = mStringsService.loadStrings(toStringsPsiFile);
        Map<String, AndroidString> toStringsMap = toAndroidStrings.stream().collect(Collectors.toMap(AndroidString::getName, androidString -> androidString));
        List<AndroidString> translatedStrings = doTranslate(progressIndicator, toLanguage, toStringsMap, isOverwriteExistingString);
        writeTranslatedStrings(progressIndicator, new File(toStringsPsiFile.getVirtualFile().getPath()), translatedStrings);
      } else {
        List<AndroidString> translatedStrings = doTranslate(progressIndicator, toLanguage, null, isOverwriteExistingString);
        File stringsFile = mStringsService.getStringsFile(resourceDir, toLanguage);
        writeTranslatedStrings(progressIndicator, stringsFile, translatedStrings);
      }
    }
  }

  private List<AndroidString> doTranslate(@NotNull ProgressIndicator progressIndicator, @NotNull Lang toLanguage
      , @Nullable Map<String, AndroidString> toAndroidStrings, boolean isOverwrite) {
    LOG.info("doTranslate toLanguage: " + toLanguage.getEnglishName() + ", toAndroidStrings: " + toAndroidStrings + ", isOverwrite: " + isOverwrite);

    final List<AndroidString> translatedStrings = new ArrayList<>();
    for (AndroidString androidString : mAndroidStrings) {
      if (progressIndicator.isCanceled()) break;

      final boolean translatable = androidString.isTranslatable();
      if (!translatable) continue;

      if (!isOverwrite && toAndroidStrings != null && toAndroidStrings.containsKey(androidString.getName())) {
        AndroidString toAndroidString = toAndroidStrings.get(androidString.getName());
        translatedStrings.add(toAndroidString);
        continue;
      }

      final AndroidString translatedString = androidString.clone();
      final List<AndroidString.Content> contents = translatedString.getContents();
      for (AndroidString.Content content : contents) {
        if (progressIndicator.isCanceled()) break;
        if (content.isIgnore()) continue;

        String translatedText = mTranslatorService.doTranslate(Lang.AUTO, toLanguage, content.getText());
        content.setText(translatedText);
      }
      translatedStrings.add(translatedString);
    }
    return translatedStrings;
  }

  private void writeTranslatedStrings(@NotNull ProgressIndicator progressIndicator, @NotNull File stringsFile, @NotNull List<AndroidString> translatedStrings) {
    LOG.info("writeTranslatedStrings stringsFile: " + stringsFile + ", translatedStrings: " + translatedStrings);

    if (progressIndicator.isCanceled() || translatedStrings.isEmpty()) return;

    progressIndicator.setText("Writing to " + stringsFile.getParentFile().getName() + " data...");
    mStringsService.writeStringsFile(translatedStrings, stringsFile);

    refreshAndOpenFile(stringsFile);
  }

  private void refreshAndOpenFile(File file) {
    VirtualFile virtualFile = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(file);
    boolean isOpenTranslatedFile = PropertiesComponent.getInstance(myProject)
        .getBoolean(Constants.KEY_IS_OPEN_TRANSLATED_FILE);
    if (virtualFile != null && isOpenTranslatedFile) {
      ApplicationManager.getApplication().invokeLater(() ->
          FileEditorManager.getInstance(myProject).openFile(virtualFile, true));
    }
  }

  @Override
  public void onSuccess() {
    super.onSuccess();
    translateSuccess();
  }

  @Override
  public void onThrowable(@NotNull Throwable error) {
    super.onThrowable(error);
    translateError(error);
  }

  private void translateSuccess() {
    if (mOnTranslateListener != null) {
      mOnTranslateListener.onTranslateSuccess();
    }
  }

  private void translateError(Throwable error) {
    if (mOnTranslateListener != null) {
      mOnTranslateListener.onTranslateError(error);
    }
  }

}
