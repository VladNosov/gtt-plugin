package ui.tree;

import com.intellij.ui.ColoredTreeCellRenderer;
import org.jetbrains.annotations.NotNull;
import ui.nodes.AbstractNode;
import javax.swing.*;
import java.awt.event.MouseEvent;

/**
 * Класс для визуализции ячейки дерева
 * Created by v.nosov on 19.06.2017.
 */
public class TreeCellRenderer extends ColoredTreeCellRenderer {
    private String iconToolTip = null;

    @Override
    public void customizeCellRenderer(@NotNull JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        AbstractNode node = (AbstractNode) value;
        node.render(this);
    }

    public void setIconToolTip(String toolTip) {
        this.iconToolTip = toolTip;
    }

    @Override
    public String getToolTipText(MouseEvent event) {
        if (iconToolTip == null) {
            return super.getToolTipText(event);
        }

        if (event.getX() < getIconWidth()) {
            return iconToolTip;
        }

        return super.getToolTipText(event);
    }

    private int getIconWidth() {
        if (getIcon() != null) {
            return getIcon().getIconWidth() + myIconTextGap;
        }
        return 0;
    }
}