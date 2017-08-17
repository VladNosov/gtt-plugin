package project.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.ui.tree.TreeUtil;
import org.jetbrains.annotations.NotNull;
import project.actions.utils.GtTActions;
import util.GtTDataKeys;

/**
 * Экшн - свернуть все ветки дерева
 * Created by v.nosov on 20.06.2017.
 */
public class CollapseAllAction extends GtTActions {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Tree tree = GtTDataKeys.CASES_TREE.getData(e.getDataContext());
        if (tree == null) return;
        TreeUtil.collapseAll(tree, 1);
    }
}