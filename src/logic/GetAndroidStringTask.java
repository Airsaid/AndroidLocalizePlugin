package logic;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.psi.xml.XmlDocument;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import module.AndroidString;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author airsaid
 */
public class GetAndroidStringTask extends Task.Backgroundable {

    private PsiFile mFile;
    private OnGetAndroidStringListener mListener;
    private List<AndroidString> mAndroidStrings = new ArrayList<>();

    public interface OnGetAndroidStringListener {
        void onGetSuccess(@NotNull List<AndroidString> list);

        void onGetError(@NotNull Throwable error);
    }

    public GetAndroidStringTask(@Nullable Project project, @Nls @NotNull String title, PsiFile file) {
        super(project, title);
        this.mFile = file;
    }

    @Override
    public void run(@NotNull ProgressIndicator progressIndicator) {
        if (!(mFile instanceof XmlFile)) {
            onGetError(new IllegalArgumentException("File is not a strings.xml file."));
            return;
        }

        ApplicationManager.getApplication().runReadAction(() -> {
            mAndroidStrings.clear();
            XmlFile file = (XmlFile) mFile;
            XmlDocument document = file.getDocument();
            if (document != null) {
                XmlTag rootTag = document.getRootTag();
                if (rootTag != null) {
                    XmlTag[] stringTags = rootTag.findSubTags("string");
                    for (XmlTag stringTag : stringTags) {
                        String name = stringTag.getAttributeValue("name");
                        String value = stringTag.getValue().getText();
                        String translatableStr = stringTag.getAttributeValue("translatable");
                        Boolean translatable = Boolean.valueOf(translatableStr == null ? "true" : translatableStr);
                        mAndroidStrings.add(new AndroidString(name, value, translatable));
                        progressIndicator.setText("Loading " + name + " text from strings.xml...");
                    }
                }
            }
        });
    }

    public void setOnGetAndroidStringListener(OnGetAndroidStringListener listener) {
        this.mListener = listener;
    }

    @Override
    public void onSuccess() {
        super.onSuccess();
        onGetSuccess(mAndroidStrings);
    }

    @Override
    public void onThrowable(@NotNull Throwable error) {
        super.onThrowable(error);
        onGetError(error);
    }

    private void onGetSuccess(@NotNull List<AndroidString> list) {
        if (mListener != null) {
            mListener.onGetSuccess(list);
        }
    }

    private void onGetError(Throwable error) {
        if (mListener != null) {
            mListener.onGetError(error);
        }
    }
}
