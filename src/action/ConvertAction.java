package action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import ui.ChoiceLanguageDialog;

/**
 * @author airsaid
 */
public class ConvertAction extends AnAction implements ChoiceLanguageDialog.OnClickListener {

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getData(CommonDataKeys.PROJECT);
        ChoiceLanguageDialog dialog = new ChoiceLanguageDialog(project);
        dialog.setOnClickListener(this);
        dialog.show();
    }

    @Override
    public void onClickListener() {
        System.out.println("click");
    }
}
