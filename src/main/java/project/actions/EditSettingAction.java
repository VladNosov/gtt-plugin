package project.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.*;
import project.actions.utils.GtTActions;
import project.actions.utils.GtTActionsUtil;

/**
 * Экшн - открытие меню настрое
 * Created by v.nosov on 02.06.2017.
 */
public class EditSettingAction extends GtTActions {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        showSettingsFor(GtTActionsUtil.getProject(e.getDataContext()));
    }

    protected static void showSettingsFor(@Nullable Project project) {
        ShowSettingsUtil.getInstance().showSettingsDialog(project, "GtT");
    }
}
