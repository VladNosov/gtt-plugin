package api.testrail.data;

import com.google.gson.annotations.SerializedName;

/**
 * Кейс TestRail
 * Created by v.nosov on 09.06.2017.
 */
public class Case {
    @SerializedName("id") private int caseId;
    @SerializedName("title") private String title;
    @SerializedName("section_id") private int sectionId;
    @SerializedName("template_id") private int templateId;
    @SerializedName("type_id") private int typeId;
    @SerializedName("priority_id") private int priorityId;
    @SerializedName("milestone_id") private int mielstoneId;
    @SerializedName("refs") private String refs;
    @SerializedName("created_by") private int createdBy;
    @SerializedName("created_on") private long createdOn;
    @SerializedName("updated_by") private int updatedBy;
    @SerializedName("updated_on") private long updatedOn;
    @SerializedName("estimate") private String estimate; //todo исправить тип
    @SerializedName("estimate_forecast") private String estimateForecast; //todo исправить тип
    @SerializedName("suite_id") private int suiteId;
    // custom fields
    @SerializedName("custom_specification") private String specification;
    @SerializedName("custom_state") private int state;
    @SerializedName("custom_toregress") private boolean toRegress;
    @SerializedName("custom_dynparam") private boolean dynparam;
    @SerializedName("custom_preconds") private String preconds;
    @SerializedName("custom_steps") private String steps;
    @SerializedName("custom_test_steps") private String testSteps;
    @SerializedName("custom_test_data") private String testData;

    public Case(int caseId, String title, int sectionId, int templateId, int typeId, int priorityId, int mielstoneId,
                String refs, int createdBy, long createdOn, int updatedBy, long updatedOn, String estimate, String estimateForecast,
                int suiteId, String specification, int state, boolean toRegress, boolean dynparam, String preconds, String steps,
                String testSteps, String testData) {
        this.caseId = caseId;
        this.title = title;
        this.sectionId = sectionId;
        this.templateId = templateId;
        this.typeId = typeId;
        this.priorityId = priorityId;
        this.mielstoneId = mielstoneId;
        this.refs = refs;
        this.createdBy = createdBy;
        this.createdOn = createdOn;
        this.updatedBy = updatedBy;
        this.updatedOn = updatedOn;
        this.estimate = estimate;
        this.estimateForecast = estimateForecast;
        this.suiteId = suiteId;
        this.specification = specification;
        this.state = state;
        this.toRegress = toRegress;
        this.dynparam = dynparam;
        this.preconds = preconds;
        this.steps = steps;
        this.testSteps = testSteps;
        this.testData = testData;
    }

    public int getCaseId() {
        return caseId;
    }

    public int getPriorityId() {
        return priorityId;
    }

    public String getTitle() {
        return title;
    }
}
