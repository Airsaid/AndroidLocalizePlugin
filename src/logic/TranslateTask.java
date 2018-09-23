package logic;

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

        /**
         * translate success/done.
         */
        void onTranslateSuccess();

        /**
         * translate fail.
         *
         * @param e error.
         */
        void onTranslateFail(Throwable e);
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
        for (LANG toLanguage : mLanguages) {
            List<AndroidString> writeAndroidString = new ArrayList<>();
            for (AndroidString androidString : mAndroidStrings) {
                if (androidString.isTranslatable()) {
                    querierTrans.setParams(LANG.English, toLanguage, androidString.getValue());
                    String resultValue = querierTrans.executeSingle();
                    writeAndroidString.add(new AndroidString(androidString.getName(), resultValue, false));
                }
            }
            mWriteData.put(toLanguage.getCode(), writeAndroidString);
        }
        googleTranslator.close();
        writeResultData();
    }

    private void writeResultData() {
        if (mWriteData == null) {
            translateFail(new IllegalArgumentException("No translate data."));
            return;
        }

        Set<String> keySet = mWriteData.keySet();
        for (String key : keySet) {
            File writeFile = getWriteFileForCode(key);
            List<AndroidString> values = mWriteData.get(key);
            write(writeFile, values);
            refreshAndOpenFile(writeFile);
        }
    }

    private File getWriteFileForCode(String langCode) {
        String parentPath = mSelectFile.getParent().getParent().getPath();
        File parentFile = new File(parentPath, "values-".concat(langCode));
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
        translateFail(error);
    }

    private void translateSuccess() {
        if (mOnTranslateListener != null) {
            mOnTranslateListener.onTranslateSuccess();
        }
    }

    private void translateFail(Throwable error) {
        if (mOnTranslateListener != null) {
            mOnTranslateListener.onTranslateFail(error);
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
