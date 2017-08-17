package project.actions.utils;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.project.DumbAware;

/**
 * Created by v.nosov on 05.06.2017.
 */
public abstract class GtTActions extends AnAction implements DumbAware {
    @Override
    public void update(AnActionEvent e) {
        super.update(e);
        Presentation p = e.getPresentation();
        p.setEnabled(isAvailable(e));
        p.setVisible(isVisible(e));
    }

    protected boolean isAvailable(AnActionEvent e) {
        return GtTActionsUtil.hasProject(e.getDataContext());
    }

    protected boolean isVisible(AnActionEvent e) {
        return true;
    }
}
