package api.testrail.data;

/**
 * Тип кейса в TestRail
 * Created by v.nosov on 09.06.2017.
 */
public class CaseType {
    private int id;
    private String name;
    private boolean isDefault;

    public CaseType(int id, String name, boolean isDefault) {
        this.id = id;
        this.name = name;
        this.isDefault = isDefault;
    }
}