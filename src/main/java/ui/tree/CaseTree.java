package ui.tree;

import com.intellij.openapi.actionSystem.*;
import com.intellij.ui.PopupHandler;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.EditSourceOnDoubleClickHandler;
import com.intellij.util.EditSourceOnEnterKeyHandler;
import com.intellij.util.ui.UIUtil;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.Nullable;
import javax.swing.tree.TreeModel;

/**
 * UI-компонент - дерево кейсов
 * Created by v.nosov on 20.06.2017.
 */
public class CaseTree extends Tree implements DataProvider {

    public CaseTree(TreeModel model) {
        super(model);
        init();
    }

    private void init() {
        UIUtil.setLineStyleAngled(this);
        this.setShowsRootHandles(false);
        this.setCellRenderer(new TreeCellRenderer());
        this.expandRow(0);

        //настраиваем попап с экшнами для элементов дерева
        ActionManager manager = ActionManager.getInstance();
        DefaultActionGroup group = new DefaultActionGroup();
        group.add(manager.getAction("GtT.ParseToGherkin"));
        PopupHandler.installPopupHandler(this, group, ActionPlaces.TODO_VIEW_POPUP, manager);

        EditSourceOnDoubleClickHandler.install(this);
        EditSourceOnEnterKeyHandler.install(this);
    }

    @Nullable
    @Override
    public Object getData(@NonNls String dataId) {
        //todo добавить логику

        return null;
    }
}
