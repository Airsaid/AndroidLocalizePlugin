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

package com.airsaid.localization.task;

import com.airsaid.localization.constant.Constants;
import com.airsaid.localization.services.AndroidValuesService;
import com.airsaid.localization.translate.TranslationException;
import com.airsaid.localization.translate.lang.Lang;
import com.airsaid.localization.translate.lang.Languages;
import com.airsaid.localization.translate.services.TranslatorService;
import com.airsaid.localization.utils.TextUtil;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.xml.*;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author airsaid
 */
public class TranslateTask extends Task.Backgroundable {

  private static final String NAME_TAG_STRING = "string";
  private static final String NAME_TAG_PLURALS = "plurals";
  private static final String NAME_TAG_STRING_ARRAY = "string-array";

  private static final Logger LOG = Logger.getInstance(TranslateTask.class);

  private final List<Lang> mToLanguages;
  private final List<PsiElement> mValues;
  private final VirtualFile mValueFile;
  private final TranslatorService mTranslatorService;
  private final AndroidValuesService mValueService;

  private OnTranslateListener mOnTranslateListener;
  private TranslationException mTranslationError;

  public interface OnTranslateListener {
    void onTranslateSuccess();

    void onTranslateError(Throwable e);
  }

  public TranslateTask(@Nullable Project project, @Nls @NotNull String title, List<Lang> languages,
                       List<PsiElement> values, PsiFile valueFile) {
    super(project, title);
    mToLanguages = languages;
    mValues = values;
    mValueFile = valueFile.getVirtualFile();
    mTranslatorService = TranslatorService.getInstance();
    mValueService = AndroidValuesService.getInstance();
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
    boolean isOverwriteExistingString = PropertiesComponent.getInstance(myProject)
        .getBoolean(Constants.KEY_IS_OVERWRITE_EXISTING_STRING);
    LOG.info("run isOverwriteExistingString: " + isOverwriteExistingString);

    for (Lang toLanguage : mToLanguages) {
      if (progressIndicator.isCanceled()) break;

      progressIndicator.setText("Translation to " + toLanguage.getEnglishName() + "...");

      VirtualFile resourceDir = mValueFile.getParent().getParent();
      String valueFileName = mValueFile.getName();
      PsiFile toValuePsiFile = mValueService.getValuePsiFile(myProject, resourceDir, toLanguage, valueFileName);
      LOG.info("Translating language: " + toLanguage.getEnglishName() + ", toValuePsiFile: " + toValuePsiFile);
      if (toValuePsiFile != null) {
        List<PsiElement> toValues = mValueService.loadValues(toValuePsiFile);
        Map<String, PsiElement> toValuesMap = toValues.stream().collect(Collectors.toMap(
            psiElement -> {
              if (psiElement instanceof XmlTag)
                return ApplicationManager.getApplication().runReadAction((Computable<String>) () ->
                    ((XmlTag) psiElement).getAttributeValue("name"));
              else return UUID.randomUUID().toString();
            },
            Function.identity()
        ));
        List<PsiElement> translatedValues = doTranslate(progressIndicator, toLanguage, toValuesMap, isOverwriteExistingString);
        writeTranslatedValues(progressIndicator, new File(toValuePsiFile.getVirtualFile().getPath()), translatedValues);
      } else {
        List<PsiElement> translatedValues = doTranslate(progressIndicator, toLanguage, null, isOverwriteExistingString);
        File valueFile = mValueService.getValueFile(resourceDir, toLanguage, valueFileName);
        writeTranslatedValues(progressIndicator, valueFile, translatedValues);
      }
      // If an exception occurs during the translation of the language,
      // the translation of the subsequent languages is terminated.
      // This prevents the loss of successfully translated strings in that language.
      if (mTranslationError != null) {
        throw mTranslationError;
      }
    }
  }

  private List<PsiElement> doTranslate(@NotNull ProgressIndicator progressIndicator,
                                       @NotNull Lang toLanguage,
                                       @Nullable Map<String, PsiElement> toValues,
                                       boolean isOverwrite) {
    LOG.info("doTranslate toLanguage: " + toLanguage.getEnglishName() + ", toValues: " + toValues + ", isOverwrite: " + isOverwrite);

    List<PsiElement> translatedValues = new ArrayList<>();
    for (PsiElement value : mValues) {
      if (progressIndicator.isCanceled()) break;

      if (value instanceof XmlTag) {
        XmlTag xmlTag = (XmlTag) value;
        if (!mValueService.isTranslatable(xmlTag)) {
          translatedValues.add(value);
          continue;
        }

        String name = ApplicationManager.getApplication().runReadAction((Computable<String>) () ->
            xmlTag.getAttributeValue("name")
        );
        if (!isOverwrite && toValues != null && toValues.containsKey(name)) {
          translatedValues.add(toValues.get(name));
          continue;
        }

        XmlTag translateValue = ApplicationManager.getApplication().runReadAction((Computable<XmlTag>) () ->
            (XmlTag) xmlTag.copy()
        );
        translatedValues.add(translateValue);
        switch (translateValue.getName()) {
          case NAME_TAG_STRING:
            doTranslate(progressIndicator, toLanguage, translateValue);
            break;
          case NAME_TAG_STRING_ARRAY:
          case NAME_TAG_PLURALS:
            XmlTag[] subTags = ApplicationManager.getApplication()
                .runReadAction((Computable<XmlTag[]>) translateValue::getSubTags);
            for (XmlTag subTag : subTags) {
              doTranslate(progressIndicator, toLanguage, subTag);
            }
            break;
        }
      } else {
        translatedValues.add(value);
      }
    }
    return translatedValues;
  }

  private void doTranslate(@NotNull ProgressIndicator progressIndicator,
                           @NotNull Lang toLanguage,
                           @NotNull XmlTag xmlTag) {
    if (progressIndicator.isCanceled() || isXliffTag(xmlTag)) return;

    XmlTagValue xmlTagValue = ApplicationManager.getApplication()
        .runReadAction((Computable<XmlTagValue>) xmlTag::getValue);
    XmlTagChild[] children = xmlTagValue.getChildren();
    for (XmlTagChild child : children) {
      if (child instanceof XmlText) {
        XmlText xmlText = (XmlText) child;
        String text = ApplicationManager.getApplication()
            .runReadAction((Computable<String>) xmlText::getValue);
        if (TextUtil.isEmptyOrSpacesLineBreak(text)) {
          continue;
        }
        try {
          String translatedText = mTranslatorService.doTranslate(Languages.AUTO, toLanguage, text);
          ApplicationManager.getApplication().runReadAction(() -> xmlText.setValue(translatedText));
        } catch (TranslationException e) {
          LOG.warn(e);
          // Just catch the error and wait for that file to be translated and released.
          mTranslationError = e;
        }
      } else if (child instanceof XmlTag) {
        doTranslate(progressIndicator, toLanguage, (XmlTag) child);
      }
    }
  }

  private void writeTranslatedValues(@NotNull ProgressIndicator progressIndicator,
                                     @NotNull File valueFile,
                                     @NotNull List<PsiElement> translatedValues) {
    LOG.info("writeTranslatedValues valueFile: " + valueFile + ", translatedValues: " + translatedValues);

    if (progressIndicator.isCanceled() || translatedValues.isEmpty()) return;

    progressIndicator.setText("Writing to " + valueFile.getParentFile().getName() + " data...");
    mValueService.writeValueFile(translatedValues, valueFile);

    refreshAndOpenFile(valueFile);
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

  private boolean isXliffTag(XmlTag xmlTag) {
    return xmlTag != null && "xliff:g".equals(xmlTag.getName());
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
