package ui;

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

    private OnClickListener mOnClickListener;

    public interface OnClickListener {
        void onClickListener();
    }

    public ChoiceLanguageDialog(@Nullable Project project) {
        super(project, false);
        setTitle("Choice Convert Language");
        setResizable(true);
        setSize(800, 630);
        init();
    }

    @Override
    protected void doOKAction() {
        super.doOKAction();
        if (mOnClickListener != null) {
            mOnClickListener.onClickListener();
        }
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {

        return null;
    }

    /**
     * 设置点击事件。
     *
     * @param listener 监听回调接口
     */
    public void setOnClickListener(OnClickListener listener) {
        mOnClickListener = listener;
    }

}
