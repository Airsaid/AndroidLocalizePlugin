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

package task;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import constant.Constants;
import logic.ParseStringXml;
import module.AndroidString;
import module.Content;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import translate.lang.LANG;
import translate.querier.Querier;
import translate.trans.AbstractTranslator;
import translate.trans.impl.GoogleTranslator;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @author airsaid
 */
public class TranslateTask extends Task.Backgroundable {

    private List<LANG>                       mLanguages;
    private List<AndroidString>              mAndroidStrings;
    private VirtualFile                      mSelectFile;
    private Map<String, List<AndroidString>> mWriteData;
    private OnTranslateListener              mOnTranslateListener;

    public interface OnTranslateListener {
        void onTranslateSuccess();

        void onTranslateError(Throwable e);
    }

    public TranslateTask(@Nullable Project project, @Nls @NotNull String title, List<LANG> languages,
                         List<AndroidString> androidStrings, VirtualFile selectFile) {
        super(project, title);
        this.mLanguages = languages;
        this.mAndroidStrings = androidStrings;
        this.mSelectFile = selectFile;
        this.mWriteData = new HashMap<>();
    }

    @Override
    public void run(@NotNull ProgressIndicator progressIndicator) {
        boolean isOverwriteExistingString = PropertiesComponent.getInstance(myProject)
                .getBoolean(Constants.KEY_IS_OVERWRITE_EXISTING_STRING);

        Querier<AbstractTranslator> translator = new Querier<>();
        GoogleTranslator googleTranslator = new GoogleTranslator();
        translator.attach(googleTranslator);
        mWriteData.clear();

        for (LANG toLanguage : mLanguages) {
            progressIndicator.setText("Translating in the " + toLanguage.getEnglishName() + " language...");

            if (isOverwriteExistingString) {
                translate(translator, toLanguage, null);
                continue;
            }

            ApplicationManager.getApplication().runReadAction(() -> {
                VirtualFile virtualFile = getVirtualFile(toLanguage);

                if (virtualFile == null) {
                    translate(translator, toLanguage, null);
                    return;
                }

                PsiFile psiFile = PsiManager.getInstance(myProject).findFile(virtualFile);
                if (psiFile == null) {
                    translate(translator, toLanguage, null);
                    return;
                }

                List<AndroidString> androidStrings = ParseStringXml.parse(progressIndicator, psiFile);
                translate(translator, toLanguage, androidStrings);
            });
        }
        googleTranslator.close();
        writeResultData(progressIndicator);
    }

    private void translate(Querier<AbstractTranslator> translator, LANG toLanguage, @Nullable List<AndroidString> list) {
        List<AndroidString> writeAndroidString = new ArrayList<>();
        for (AndroidString androidString : mAndroidStrings) {
            if (!androidString.isTranslatable()) {
                continue;
            }

            // If the string to be translated already exists, use it directly
            if (list != null && list.contains(androidString)) {
                writeAndroidString.add(list.get(list.indexOf(androidString)));
                continue;
            }

            AndroidString clone = androidString.clone();
            List<Content> contexts = clone.getContents();
            for (Content content : contexts) {
                if (content.isIgnore()) continue; // Ignore text with xliff:g tags set

                translator.setParams(LANG.Auto, toLanguage, content.getText());
                String result = translator.executeSingle();
                content.setText(result);
            }

            writeAndroidString.add(clone);
        }
        mWriteData.put(toLanguage.getCode(), writeAndroidString);
    }

    private void writeResultData(ProgressIndicator progressIndicator) {
        if (mWriteData == null) {
            translateError(new IllegalArgumentException("No translate data."));
            return;
        }

        Set<String> keySet = mWriteData.keySet();
        for (String key : keySet) {
            File writeFile = getWriteFileForCode(key);
            progressIndicator.setText("Write to " + writeFile.getParentFile().getName() + " data...");
            write(writeFile, mWriteData.get(key));
            refreshAndOpenFile(writeFile);
        }
    }

    private VirtualFile getVirtualFile(LANG lang) {
        File file = getStringFile(lang.getCode());
        return LocalFileSystem.getInstance().findFileByIoFile(file);
    }

    private File getStringFile(String langCode) {
        return getStringFile(langCode, false);
    }

    private File getStringFile(String langCode, boolean mkdirs) {
        String parentPath = mSelectFile.getParent().getParent().getPath();
        File stringFile;
        if (mkdirs) {
            File parentFile = new File(parentPath, getDirNameForCode(langCode));
            if (!parentFile.exists()) {
                parentFile.mkdirs();
            }
            stringFile = new File(parentFile, "strings.xml");
            if (!stringFile.exists()) {
                try {
                    stringFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            stringFile = new File(parentPath.concat(File.separator).concat(getDirNameForCode(langCode)), "strings.xml");
        }
        return stringFile;
    }

    private File getWriteFileForCode(String langCode) {
        return getStringFile(langCode, true);
    }

    private String getDirNameForCode(String langCode) {
        String suffix;
        if (langCode.equals(LANG.ChineseSimplified.getCode())) {
            suffix = "zh-rCN";
        } else if (langCode.equals(LANG.ChineseTraditional.getCode())) {
            suffix = "zh-rTW";
        } else if (langCode.equals(LANG.Filipino.getCode())) {
            suffix = "fil";
        } else if (langCode.equals(LANG.Indonesian.getCode())) {
            suffix = "in-rID";
        } else if (langCode.equals(LANG.Javanese.getCode())) {
            suffix = "jv";
        } else {
            suffix = langCode;
        }
        return "values-".concat(suffix);
    }

    private void write(File file, List<AndroidString> androidStrings) {
        ApplicationManager.getApplication().invokeLater(() -> ApplicationManager.getApplication().runWriteAction(() -> {
            try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, false), StandardCharsets.UTF_8))) {
                bw.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
                bw.newLine();
                bw.write("<resources>");
                bw.newLine();
                for (AndroidString androidString : androidStrings) {
                    bw.write("\t<string name=\"" + androidString.getName() + "\">");
                    for (Content content : androidString.getContents()) {
                        bw.write(content.getText());
                    }
                    bw.write("</string>");
                    bw.newLine();
                }
                bw.write("</resources>");
                bw.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));
    }

    private void refreshAndOpenFile(File file) {
        VirtualFile virtualFile = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(file);
        if (virtualFile != null) {
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

    /**
     * Set translate result listener.
     *
     * @param listener callback interface. success or fail.
     */
    public void setOnTranslateListener(OnTranslateListener listener) {
        this.mOnTranslateListener = listener;
    }

}
