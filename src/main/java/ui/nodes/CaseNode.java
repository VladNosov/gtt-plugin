package ui.nodes;

import api.testrail.data.Case;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.ui.SimpleTextAttributes;
import org.jetbrains.annotations.NotNull;
import ui.tree.TreeCellRenderer;
import util.icons.GtTIcons;
import javax.swing.*;

/**
 * Кейс TR
 * Created by v.nosov on 19.06.2017.
 */
public class CaseNode extends AbstractNode {
    @NotNull private final Case myCase;

    public CaseNode(@NotNull Case myCase) {
        this.myCase = myCase;
    }

    /**
     * Отображение кейса
     */
    @Override
    public void render(TreeCellRenderer renderer) {
        //устанавливаем приоритет кейса
        String priority = myCase.getPriorityId() + ""; //todo добавить преобразование в строку
        String priorityStr = StringUtil.toLowerCase(priority);
        renderer.setIconToolTip(priority);
        setIcon(renderer, GtTIcons.priority12("critical"));//todo убрать заглушку

        //название кейса
        renderer.setToolTipText("Double click to parse in gherkin feature");
        renderer.append(myCase.getTitle());

        renderer.append(" ");
        //todo добавить отрисовку остальных атрибутов кейса
        renderer.append("id: " + myCase.getPriorityId(), SimpleTextAttributes.GRAY_ATTRIBUTES);
    }

    private void setIcon(TreeCellRenderer renderer, Icon icon) {
        renderer.setIcon(icon);
    }

    @Override
    public int getCaseCount() {
        return 1;
    }

    public Case getCase() {
        return myCase;
    }

    public int id() {
        return myCase.getCaseId();
    }
}