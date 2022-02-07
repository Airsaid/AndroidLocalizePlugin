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
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.xml.XmlComment;
import com.intellij.psi.xml.XmlTag;
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

            progressIndicator.setText("Translating in the " + toLanguage.getEnglishName() + " language...");

            VirtualFile resourceDir = mValueFile.getParent().getParent();
            String valueFileName = mValueFile.getName();
            PsiFile toValuePsiFile = mValueService.getValuePsiFile(myProject, resourceDir, toLanguage, valueFileName);
            LOG.info("Translating language: " + toLanguage.getEnglishName() + ", toValuePsiFile: " + toValuePsiFile);
            if (toValuePsiFile != null) {
                List<PsiElement> toValues = mValueService.loadValues(toValuePsiFile);


                Map<String, PsiElement> toValuesMap = toValues.stream().collect(Collectors.toMap(
                        psiElement -> {
                            if (psiElement instanceof XmlTag) return ((XmlTag) psiElement).getAttributeValue("name");
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
        }
    }

    private List<PsiElement> doTranslate(@NotNull ProgressIndicator progressIndicator, @NotNull Lang toLanguage
            , @Nullable Map<String, PsiElement> toValues, boolean isOverwrite) {
        LOG.info("doTranslate toLanguage: " + toLanguage.getEnglishName() + ", toValues: " + toValues + ", isOverwrite: " + isOverwrite);

        List<PsiElement> translatedValues = new ArrayList<>();
        for (PsiElement value : mValues) {
            if (progressIndicator.isCanceled()) break;

            if ((value instanceof XmlTag)) {
                String translatableStr = ((XmlTag) value).getAttributeValue("translatable");
                final boolean translatable = Boolean.parseBoolean(translatableStr == null ? "true" : translatableStr);
                if (!translatable) continue;


                if (!isOverwrite && toValues != null && toValues.containsKey(((XmlTag) value).getAttributeValue("name"))) {
                    PsiElement toAndroidString = toValues.get(((XmlTag) value).getAttributeValue("name"));
                    translatedValues.add(toAndroidString);
                    continue;
                }


                String tagName = ((XmlTag) value).getName();
                switch (tagName) {
                    case NAME_TAG_STRING:
                    case NAME_TAG_STRING_ARRAY:
                    case NAME_TAG_PLURALS:
                        XmlTag translateValue = ((XmlTag) value.copy());
                        translatedValues.add(translateValue);
                        doTranslate(progressIndicator, toLanguage, translateValue);
                }

            } else {
                if (value instanceof XmlComment) continue;
                translatedValues.add(value);
            }
        }
        return translatedValues;
    }

    private void doTranslate(@NotNull ProgressIndicator progressIndicator,
                             @NotNull Lang toLanguage,
                             @NotNull XmlTag xmlTag) {

        if (xmlTag.getName().equals(NAME_TAG_STRING)) {
            if (progressIndicator.isCanceled()) return;
            if (TextUtil.isEmptyOrSpacesLineBreak(xmlTag.getValue().getText())) return;

            String translatedText = mTranslatorService.doTranslate(Languages.AUTO, toLanguage, xmlTag.getValue().getText());
            xmlTag.getValue().setText(translatedText);
        } else {
            for (XmlTag subTag : xmlTag.getSubTags()) {
                if (progressIndicator.isCanceled()) break;
                if (TextUtil.isEmptyOrSpacesLineBreak(subTag.getValue().getText())) continue;

                String translatedText = mTranslatorService.doTranslate(Languages.AUTO, toLanguage, subTag.getValue().getText());
                subTag.getValue().setText(translatedText);
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
