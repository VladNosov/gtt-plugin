package api.testrail.data;

/**
 * Секция в сьюте TestRail
 * Created by v.nosov on 09.06.2017.
 */
public class Section {
    private int id;
    private int suiteId;
    private String name;
    private String description;
    private int parentId;
    private int displayOrder;
    private int depth;

    public Section(int id, int suiteId, String name, String description, int parentId, int displayOrder, int depth) {
        this.id = id;
        this.suiteId = suiteId;
        this.name = name;
        this.description = description;
        this.parentId = parentId;
        this.displayOrder = displayOrder;
        this.depth = depth;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
