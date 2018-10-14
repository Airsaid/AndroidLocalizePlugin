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
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.psi.xml.XmlFile;
import logic.ParseStringXml;
import module.AndroidString;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author airsaid
 */
public class GetAndroidStringTask extends Task.Backgroundable {

    private PsiFile mFile;
    private List<AndroidString> mAndroidStrings;
    private OnGetAndroidStringListener mListener;

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
            mAndroidStrings = ParseStringXml.parse(progressIndicator, mFile);
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
