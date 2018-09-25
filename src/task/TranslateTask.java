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

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import module.AndroidString;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import translate.lang.LANG;
import translate.querier.Querier;
import translate.trans.AbstractTranslator;
import translate.trans.impl.GoogleTranslator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * @author airsaid
 */
public class TranslateTask extends Task.Backgroundable {

    private List<LANG> mLanguages;
    private List<AndroidString> mAndroidStrings;
    private VirtualFile mSelectFile;
    private Map<String, List<AndroidString>> mWriteData;
    private OnTranslateListener mOnTranslateListener;

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
        Querier<AbstractTranslator> querierTrans = new Querier<>();
        GoogleTranslator googleTranslator = new GoogleTranslator();
        querierTrans.attach(googleTranslator);
        mWriteData.clear();
        for (LANG toLanguage : mLanguages) {
            progressIndicator.setText("Translating in the " + toLanguage.getEnglishName() + " language...");
            List<AndroidString> writeAndroidString = new ArrayList<>();
            for (AndroidString androidString : mAndroidStrings) {
                if (androidString.isTranslatable()) {
                    querierTrans.setParams(LANG.Auto, toLanguage, androidString.getValue());
                    String resultValue = querierTrans.executeSingle();
                    writeAndroidString.add(new AndroidString(androidString.getName(), resultValue, false));
                }
            }
            mWriteData.put(toLanguage.getCode(), writeAndroidString);
        }
        googleTranslator.close();
        writeResultData(progressIndicator);
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

    private File getWriteFileForCode(String langCode) {
        String parentPath = mSelectFile.getParent().getParent().getPath();
        File parentFile = new File(parentPath, getDirNameForCode(langCode));
        if (!parentFile.exists()) {
            parentFile.mkdirs();
        }
        File file = new File(parentFile, "strings.xml");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
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
        ApplicationManager.getApplication().invokeLater(() -> {
            ApplicationManager.getApplication().runWriteAction(() -> {
                try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
                    bw.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
                    bw.newLine();
                    bw.write("<resources>");
                    bw.newLine();
                    for (AndroidString androidString : androidStrings) {
                        bw.write("\t<string name=\"" + androidString.getName() + "\">" + androidString.getValue() + "</string>");
                        bw.newLine();
                    }
                    bw.write("</resources>");
                    bw.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        });
    }

    private void refreshAndOpenFile(File file) {
        VirtualFile virtualFile = LocalFileSystem.getInstance().refreshAndFindFileByPath(file.getPath());
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
