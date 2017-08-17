package project.actions;

import com.intellij.icons.AllIcons;
import com.intellij.ide.actions.RefreshAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

/**
 * Обновить список кейсов
 * Created by v.nosov on 18.06.2017.
 */
public class RefreshCasesAction extends RefreshAction {

    public RefreshCasesAction() {
        super("Refresh", "Обновить список отфильтрованных кейсов", AllIcons.Actions.Refresh);
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        //todo добавить логику
    }

    @Override
    public void update(AnActionEvent e) {
        //todo добавить логику
    }
}