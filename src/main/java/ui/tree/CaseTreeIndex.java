package ui.tree;

import ui.nodes.CaseNode;
import java.util.*;

/**
 * Created by v.nosov on 20.06.2017.
 */
public class CaseTreeIndex {
    private final Map<Integer, CaseNode> caseNodes = new HashMap<>();

    public CaseNode getCaseNode(int caseId) {
        return caseNodes.get(caseId);
    }

    public void setCaseNode(CaseNode node) {
        caseNodes.put(node.id(), node);
    }

    public void remove(int caseId) {
        caseNodes.remove(caseId);
    }

    public void clear() {
        caseNodes.clear();
    }

    public Set<Integer> getAllCases() {
        return caseNodes.keySet();
    }
}