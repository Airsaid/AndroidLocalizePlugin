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

package com.airsaid.localization.action;

import com.airsaid.localization.config.SettingsState;
import com.airsaid.localization.services.AndroidValuesService;
import com.airsaid.localization.task.TranslateTask;
import com.airsaid.localization.translate.lang.Lang;
import com.airsaid.localization.ui.SelectLanguagesDialog;
import com.airsaid.localization.utils.NotificationUtil;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.XmlRecursiveElementVisitor;
import com.intellij.psi.xml.XmlTag;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Translate android string value to other languages that can be used to localize your Android APP.
 *
 * @author airsaid
 */
public class TranslateAction extends AnAction implements SelectLanguagesDialog.OnClickListener {

    private Project mProject;
    private PsiFile mValueFile;
    private List<PsiElement> mValues;
    private final AndroidValuesService mValueService = AndroidValuesService.getInstance();
    private static final Logger LOG = Logger.getInstance(TranslateAction.class);

    @Override
    public void actionPerformed(AnActionEvent e) {
        mProject = e.getProject();
        mValueFile = e.getData(CommonDataKeys.PSI_FILE);

        SettingsState.getInstance().initSetting();

        mValueService.loadValuesByAsync(mValueFile, values -> {
            if (!isTranslatable(values)) {
                NotificationUtil.notifyInfo(mProject, "The " + mValueFile.getName() + " has no text to translate.");
                return;
            }
            mValues = values;
            showSelectLanguageDialog();
        });
    }

    // Verify that there is a text in the value file that needs to be translated.
    private boolean isTranslatable(@NotNull List<PsiElement> values) {
        for (PsiElement psiElement : values) {
            if (psiElement instanceof XmlTag) {
                if (mValueService.isTranslatable((XmlTag) psiElement)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 检查XML标签是否可翻译
     *
     * @param tag XML标签元素
     * @return 如果标签需要翻译返回true
     */
    private boolean isTranslatable(@NotNull XmlTag tag) {
        // 1. 必须是 <string*> 标签
        if (!tag.getName().startsWith("string"))
            return false;
        // 2. translatable 属性不存在 或 不为 false
        String translatable = tag.getAttributeValue("translatable");
        return translatable == null || !translatable.equals("false");
    }

    private void showSelectLanguageDialog() {
        SelectLanguagesDialog dialog = new SelectLanguagesDialog(mProject);
        dialog.setOnClickListener(this);
        dialog.show();
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        // 初始状态设为不可见
        e.getPresentation().setEnabledAndVisible(false);

        Project project = e.getProject();
        if (project == null) return;

        // 在 BGT 线程中安全访问 PSI
        ReadAction.run(() -> {
            PsiFile psiFile = e.getData(CommonDataKeys.PSI_FILE);
            if (psiFile == null || !psiFile.isValid()) return;

            // 执行 PSI 检查逻辑
            boolean isValueFile = isValueFile(psiFile);
            e.getPresentation().setEnabledAndVisible(isValueFile);
        });
    }

    private boolean isValueFile(PsiFile file) {
        //return ReadAction.compute(()->mValueService.isValueFile(file));
        return ReadAction.compute(() -> {
            if (!file.getName().endsWith(".xml") || !file.isValid())
                return false;

            final boolean[] found = {false};
            file.accept(new XmlRecursiveElementVisitor() {
                @Override
                public void visitXmlTag(@NotNull XmlTag tag) {
                    if (found[0]) return;
                    if (isTranslatable(tag))
                        found[0] = true;
                    super.visitXmlTag(tag);
                }
            });
            return found[0];
        });
    }

    @Override
    public void onClickListener(List<Lang> selectedLanguage) {
        TranslateTask translationTask = new TranslateTask(mProject, "Translating...", selectedLanguage, mValues, mValueFile);
        translationTask.setOnTranslateListener(new TranslateTask.OnTranslateListener() {
            @Override
            public void onTranslateSuccess() {
                NotificationUtil.notifyInfo(mProject, "Translation completed!");
            }

            @Override
            public void onTranslateError(Throwable e) {
                NotificationUtil.notifyError(mProject, "Translation failure: " + e.getLocalizedMessage());
            }
        });
        translationTask.queue();
    }
}
