package api.testrail.data;

/**
 * TDO проекта в TestRail
 * Created by v.nosov on 09.06.2017.
 */
public class Project {
    private int id;
    private String name;
    private String announcement;
    private boolean showAnnouncement;
    private boolean isCompleted;
    private String completedOn;
    private int suiteMode;
    private String url;

    public Project(int id, String name, String announcement, boolean showAnnouncement, boolean isCompleted, String completedOn, int suiteMode, String url) {
        this.id = id;
        this.name = name;
        this.announcement = announcement;
        this.showAnnouncement = showAnnouncement;
        this.isCompleted = isCompleted;
        this.completedOn = completedOn;
        this.suiteMode = suiteMode;
        this.url = url;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
