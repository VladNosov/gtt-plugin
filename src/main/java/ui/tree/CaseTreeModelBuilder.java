package ui.tree;

import api.testrail.data.Case;
import ui.nodes.AbstractNode;
import ui.nodes.CaseNode;
import ui.nodes.SummaryNode;
import javax.swing.tree.DefaultTreeModel;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Класс-билдер для дерева с кейсами
 * Created by v.nosov on 20.06.2017.
 */
public class CaseTreeModelBuilder {

    private final CaseTreeIndex index;
    private SummaryNode summary;
    private DefaultTreeModel model;

    public CaseTreeModelBuilder() {
        this.index = new CaseTreeIndex();
    }

    /**
     * Creates the model with a basic root
     */
    public DefaultTreeModel createModel() {
        summary = new SummaryNode();
        model = new DefaultTreeModel(summary);
        model.setRoot(summary);
        return model;
    }

    /**
     *Получить заголовок дерева
     */
    private AbstractNode getCasesParent() {
        return summary;
    }

    public void updateModel(Collection<Case> cases, String emptyText) {
        summary.setEmptyText(emptyText);

        List<Integer> toRemove = index.getAllCases().stream()
                .filter(f -> !cases.stream().anyMatch(a -> f == a.getCaseId())).collect(Collectors.toList());

        toRemove.forEach(this::removeFile);
        cases.forEach(c -> setCase(c));
        model.nodeChanged(summary);
    }

    private void setCase(Case myCase) {
        boolean newFile = false;
        CaseNode fNode = index.getCaseNode(myCase.getCaseId());
        if (fNode == null) {
            newFile = true;
            fNode = new CaseNode(myCase);
            index.setCaseNode(fNode);
        }

        if (newFile) {
            AbstractNode parent = getCasesParent();
            int idx = parent.getInsertIdx(fNode, new CaseNodeComparator());
            int[] newIdx = {idx};
            model.nodesWereInserted(parent, newIdx);
            model.nodeChanged(parent);
        } else {
            model.nodeStructureChanged(fNode);
        }
    }

    private void removeFile(Integer caseId) {
        CaseNode node = index.getCaseNode(caseId);

        if (node != null) {
            index.remove(node.id());
            model.removeNodeFromParent(node);
        }
    }

    /**
     * Класс для сортировки кейсов
     */
    private static class CaseNodeComparator implements Comparator<CaseNode> {
        @Override public int compare(CaseNode o1, CaseNode o2) {
            int c = o1.getCase().getTitle().compareTo(o2.getCase().getTitle());
            if (c != 0) {
                return c;
            }
            return o1.getCase().getCaseId() - o2.getCase().getCaseId();
        }
    }
}