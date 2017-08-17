package project.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.ui.tree.TreeUtil;
import org.jetbrains.annotations.NotNull;
import project.actions.utils.GtTActions;
import util.GtTDataKeys;

/**
 * Экшн - раскрыть все ветки дерева
 * Created by v.nosov on 20.06.2017.
 */
public class ExpandAllAction extends GtTActions {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Tree tree = e.getData(GtTDataKeys.CASES_TREE);
        if (tree == null) return;
        TreeUtil.expandAll(tree);
    }
}