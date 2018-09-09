package logic;

import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import data.SupportLanguage;
import module.AndroidString;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author airsaid
 */
public class TranslationTask extends Task.Backgroundable {

    private List<SupportLanguage> mLanguages;
    private List<AndroidString> mAndroidStrings;

    public TranslationTask(@Nullable Project project, @Nls @NotNull String title, List<SupportLanguage> languages, List<AndroidString> androidStrings) {
        super(project, title);
        this.mLanguages = languages;
        this.mAndroidStrings = androidStrings;
    }

    @Override
    public void run(@NotNull ProgressIndicator progressIndicator) {

    }
}
