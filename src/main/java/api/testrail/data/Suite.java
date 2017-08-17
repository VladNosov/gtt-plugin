package api.testrail.data;

/**
 * Cьюты TestRail
 * Created by Tester on 09.06.2017.
 */
public class Suite {
    private int id;
    private String name;
    private String discription;
    private int projectId;
    private boolean isMaster;
    private boolean isBaseLine;
    private boolean isCompleted;
    private long completedOn;
    private String url;

    public Suite(int id, String name, String discription, int projectId, boolean isMaster, boolean isBaseLine,
                 boolean isCompleted, long completedOn, String url) {
        this.id = id;
        this.name = name;
        this.discription = discription;
        this.projectId = projectId;
        this.isMaster = isMaster;
        this.isBaseLine = isBaseLine;
        this.isCompleted = isCompleted;
        this.completedOn = completedOn;
        this.url = url;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}