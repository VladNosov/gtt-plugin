package ui;

import api.testrail.TestRailAPIUtil;
import api.testrail.TestRailConnection;
import api.testrail.data.Case;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.ui.tree.TreeUtil;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.Nullable;
import ui.tree.CaseTree;
import ui.tree.CaseTreeModelBuilder;
import util.GtTNotifications;
import util.GtTSettings;
import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

/**
 * Панель с плагином
 * Created by v.nosov on 09.06.2017.
 */
public class GtTPluginPanel extends SimpleToolWindowPanel {
    public static final String ID = "GtTPlugin";

    private final GtTSettings mySettings;

    private JPanel myPane;
    private Tree myTree;
    private CaseTreeModelBuilder treeBuilder;

    public GtTPluginPanel(Project project) {
        super(true, true);
        mySettings = GtTSettings.getInstance();
        createToolbar();
        createCaseTree();
        myPane = new JPanel(new BorderLayout());
        myPane.add(ScrollPaneFactory.createScrollPane(myTree, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED), BorderLayout.CENTER);
        super.setContent(myPane);

        //todo перенести в класс-менеджер
        fillTree();
    }

    @Nullable
    public Object getData(@NonNls String dataId) {
        //todo добавить логику
        return super.getData(dataId);
    }

    public void update(Collection<Case> cases, String emptyText) {
        if (cases == null) {
            treeBuilder.updateModel(Collections.emptyList(), emptyText);
        } else {
            treeBuilder.updateModel(cases, emptyText);
        }
        TreeUtil.expandAll(myTree);
    }

    public void fillTree() {
        try(TestRailConnection connection = new TestRailConnection(mySettings.getAuthData(), true)) {
            Collection<Case> cases = TestRailAPIUtil.getCases(connection, "ATG Testing", "Master", "Sanity Check");
            update(cases, "NO CASES"); //todo добавить метод для получения mpty text
        } catch (IOException e) {
            GtTNotifications.notify("Find cases failure", String.format("Can't get case in TR: %s", e.getMessage()), NotificationType.ERROR);
        }
    }

    //=========================================== private methods ======================================================

    private void createToolbar() {
        final ActionManager actionManager = ActionManager.getInstance();
        DefaultActionGroup defaultActionGroup = (DefaultActionGroup) actionManager.getAction("GtT.ProcessesToolbar");
        ActionToolbar actionToolbar = actionManager.createActionToolbar(ID,
                defaultActionGroup, true);
        Box toolBarBox = Box.createHorizontalBox();
        toolBarBox.add(actionToolbar.getComponent());
        super.setToolbar(toolBarBox);
        actionToolbar.getComponent().setVisible(true);
    }

    private void createCaseTree() {
        treeBuilder = new CaseTreeModelBuilder();
        DefaultTreeModel model = treeBuilder.createModel();
        myTree = new CaseTree(model);
        myTree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    //todo добавить механизм парсинга при двойном клике
                    GtTNotifications.notify("case tree", "double clicked", NotificationType.INFORMATION);
                }
            }
        });
    }
}
