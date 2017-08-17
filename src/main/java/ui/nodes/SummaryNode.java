package ui.nodes;

import ui.tree.TreeCellRenderer;

/**
 * Общая информация о результате поиска
 * Created by v.nosov on 19.06.2017.
 */
public class SummaryNode extends AbstractNode {
    private String emptyText;

    public SummaryNode() {
        super();
        this.emptyText = "No cases to display";
    }

    public void setEmptyText(String emptyText) {
        this.emptyText = emptyText;
    }

    public String getText() {
        int cases = getCaseCount();

        if (cases == 0) {
            return emptyText;
        }
        return String.format("Found %d %s", cases, cases == 1 ? "case" : "cases");
    }

    @Override
    public void render(TreeCellRenderer renderer) {
        renderer.append(getText());
    }
}