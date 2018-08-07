package action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.refactoring.ui.InfoDialog;

/**
 * @author airsaid
 */
public class ConvertAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getData(CommonDataKeys.PROJECT);
        InfoDialog dialog = new InfoDialog("Info", project);
        dialog.show();
    }
}
