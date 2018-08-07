import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * 选择需要转换的语言弹框。
 *
 * @author airsaid
 */
public class ChoiceLanguageDialog extends DialogWrapper {

    protected ChoiceLanguageDialog(@Nullable Project project, boolean canBeParent) {
        super(project, canBeParent);

    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return null;
    }
}
