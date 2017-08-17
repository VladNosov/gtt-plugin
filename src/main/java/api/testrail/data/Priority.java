package api.testrail.data;

/**
 * TDO proirity in TestRail
 * Created by v.nosov on 09.06.2017.
 */
public class Priority {
    private int id;
    private String name;
    private String shortName;
    private boolean isDefault;
    private int priority;

    public Priority(int id, String name, String shortName, boolean isDefault, int priority) {
        this.id = id;
        this.name = name;
        this.shortName = shortName;
        this.isDefault = isDefault;
        this.priority = priority;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}