package ui.nodes;

import com.intellij.util.ui.UIUtil;
import org.jetbrains.annotations.NotNull;
import ui.tree.TreeCellRenderer;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;

/**
 * Абстрактный класс ветви дерева
 * Created by v.nosov on 19.06.2017.
 */
public abstract class AbstractNode<T extends AbstractNode> extends DefaultMutableTreeNode {
    protected int caseCount;

    /**
     * получить кол-во кейсов
     */
    public int getCaseCount() {
        if (caseCount < 0) {
            caseCount = 0;
            Enumeration children = super.children();
            while (children.hasMoreElements()) {
                AbstractNode node = (AbstractNode) children.nextElement();
                if (node == null) {
                    continue;
                }
                caseCount += node.getCaseCount();
            }
        }
        return caseCount;
    }

    /**
     * Отрисовка элемента в дереве
     */
    public abstract void render(TreeCellRenderer renderer);

    @Override
    public void remove(int index) {
        setDirty();
        super.remove(index);
    }

    @Override
    public void remove(MutableTreeNode aChild) {
        setDirty();
        super.remove(aChild);
    }

    @Override
    public void insert(MutableTreeNode newChild, int childIndex) {
        setDirty();
        super.insert(newChild, childIndex);
    }

    @Override
    public void add(MutableTreeNode newChild) {
        setDirty();
        super.add(newChild);
    }

    public int getInsertIdx(T newChild, Comparator<T> comparator) {
        if (children == null) {
            insert(newChild, 0);
            return 0;
        }
        int i = Collections.binarySearch(children, newChild, comparator);
        if (i >= 0) {
            throw new IllegalArgumentException("Child already exists");
        }

        int insertIdx = -i - 1;
        insert(newChild, insertIdx);
        return insertIdx;
    }

    public void setDirty() {
        caseCount = -1;
        if (super.getParent() != null) {
            ((AbstractNode) super.getParent()).setDirty();
        }
    }

    @NotNull
    protected static String spaceAndThinSpace() {
        String thinSpace = UIUtil.getLabelFont().canDisplay('\u2009') ? String.valueOf('\u2009') : " ";
        return " " + thinSpace;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "@" + hashCode();
    }
}