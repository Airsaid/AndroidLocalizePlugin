package logic;

import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import data.SupportLanguage;
import module.AndroidString;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.trans.GoogleApi;

import java.util.List;

/**
 * @author airsaid
 */
public class TranslationTask extends Task.Backgroundable {

    private List<SupportLanguage> mLanguages;
    private List<AndroidString>   mAndroidStrings;
    private final GoogleApi       mGoogleApi;

    public TranslationTask(@Nullable Project project, @Nls @NotNull String title,
                           List<SupportLanguage> languages, List<AndroidString> androidStrings) {
        super(project, title);
        this.mLanguages = languages;
        this.mAndroidStrings = androidStrings;
        mGoogleApi = new GoogleApi();
    }

    @Override
    public void run(@NotNull ProgressIndicator progressIndicator) {
        for (SupportLanguage language : mLanguages) {
            System.out.println("翻译国家:" + language.getEnglishName());
            for (AndroidString androidString : mAndroidStrings) {
                String value = androidString.getValue();
                String result = mGoogleApi.translate(value, "en", language.getCode());
                System.out.println("翻译前:" + value);
                System.out.println("翻译后:" + result);
            }
        }
    }
}
