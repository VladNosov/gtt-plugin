package api.testrail;

import com.google.gson.*;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.io.mandatory.NullCheckingFactory;
import api.testrail.TestRailConnection.ArrayPagedRequest;
import api.testrail.TestRailConnection.PagedRequest;
import api.testrail.data.*;
import exceptions.GtTConfusingException;
import exceptions.GtTJsonException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.Base64;

/**
 * Класс с методами, дергающими API TestRail-a, которые возвращают данные в TDO
 * Created by v.nosov on 07.06.2017.
 */
public class TestRailAPIUtil {
    public static final String DEFAULT_TESTRAIL_HOST = "testrail.mvideo.ru";

    private static final Header ACCEPT_JSON = new BasicHeader("Content-Type", "application/json");

    @NotNull private static final Gson gson = initGson();

    private static Gson initGson() {
        GsonBuilder builder = new GsonBuilder();
        builder.setDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        builder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
        builder.registerTypeAdapterFactory(NullCheckingFactory.INSTANCE);
        return builder.create();
    }

    //=========================================== TR API ===============================================================

    @NotNull
    public static Project getProject(@NotNull TestRailConnection connection, String projectName) throws IOException {
        List<Project> projects = getProjectList(connection);
        Project foundProject = projects.stream().filter(a -> a.getName().equals(projectName)).findFirst().orElse(null);
        if (foundProject == null) {
            throw new GtTConfusingException(String.format("Project with name '%s' not found.", projectName));
        }
        return foundProject;
    }

    @NotNull
    public static List<Project> getProjectList(@NotNull TestRailConnection connection) throws IOException {
        try {
            return loadAll(connection, "get_projects", Project[].class, ACCEPT_JSON);
        }
        catch (GtTConfusingException e) {
            e.setDetails("Can't get project list.");
            throw e;
        }
    }

    @NotNull
    public static Suite getSuite(@NotNull TestRailConnection connection, String projectName, String suiteName) throws IOException {
        Project project = getProject(connection, projectName);
        return getSuite(connection, project.getId(), suiteName);
    }

    @NotNull
    public static Suite getSuite(@NotNull TestRailConnection connection, int projectId, String suiteName) throws IOException {
        List<Suite> projects = getSuites(connection, projectId);
        Suite foundSuite = projects.stream().filter(a -> a.getName().equals(suiteName)).findFirst().orElse(null);
        if (foundSuite == null) {
            throw new GtTConfusingException(String.format("Suite with name '%s' not found.", suiteName));
        }
        return foundSuite;
    }

    @NotNull
    public static List<Suite> getSuites(@NotNull TestRailConnection connection, String projectName) throws IOException {
        Project project = getProject(connection, projectName);
        return getSuites(connection, project.getId());
    }

    @NotNull
    public static List<Suite> getSuites(@NotNull TestRailConnection connection, int projectId) throws IOException {
        try {
            return loadAll(connection, String.format("get_suites/%d", projectId), Suite[].class, ACCEPT_JSON);
        }
        catch (GtTConfusingException e) {
            e.setDetails("Can't get suites.");
            throw e;
        }
    }

    @NotNull
    public static Section getSection(@NotNull TestRailConnection connection, int projectId, int suiteId, String sectionName) throws IOException {
        List<Section> sections = getSections(connection, projectId, suiteId);
        Section foundSection = sections.stream().filter(a -> a.getName().equals(sectionName)).findFirst().orElse(null);
        if (foundSection == null) {
            throw new GtTConfusingException(String.format("Section with name '%s' not found.", sectionName));
        }
        return foundSection;
    }

    @NotNull
    public static List<Section> getSections(@NotNull TestRailConnection connection, int projectId, int suiteId) throws IOException {
        try {
            return loadAll(connection, String.format("get_sections/%d&suite_id=%d", projectId, suiteId), Section[].class, ACCEPT_JSON);
        }
        catch (GtTConfusingException e) {
            e.setDetails("Can't get sections.");
            throw e;
        }
    }

    @NotNull
    public static Case getCase(@NotNull TestRailConnection connection, int caseId) throws IOException {
        try {
            return load(connection, String.format("get_case/%d", caseId), Case.class, ACCEPT_JSON);
        }
        catch (GtTConfusingException e) {
            e.setDetails("Can't get case.");
            throw e;
        }
    }

    @NotNull
    public static List<Case> getCases(@NotNull TestRailConnection connection, String projectName, String suiteName, String sectionName) throws IOException {
        try {
            Project project = getProject(connection, projectName);
            Suite suite = getSuite(connection, project.getId(), suiteName);
            Section section = getSection(connection, project.getId(), suite.getId(), sectionName);

            return loadAll(connection, String.format("get_cases/%d&suite_id=%d&section_id=%d", project.getId(), suite.getId(), section.getId()), Case[].class, ACCEPT_JSON);
        }
        catch (GtTConfusingException e) {
            e.setDetails("Can't get cases.");
            throw e;
        }
    }


    //-------------------------------------- case attributes -----------------------------------------------------------

    @NotNull
    public static List<Priority> getPriorities(@NotNull TestRailConnection connection) throws IOException {
        try {
            return loadAll(connection, "get_priorities", Priority[].class, ACCEPT_JSON);
        }
        catch (GtTConfusingException e) {
            e.setDetails("Can't get priorities.");
            throw e;
        }
    }

    @NotNull
    public static List<CaseType> getCaseTypes(@NotNull TestRailConnection connection) throws IOException {
        try {
            return loadAll(connection, "get_case_types", CaseType[].class, ACCEPT_JSON);
        }
        catch (GtTConfusingException e) {
            e.setDetails("Can't get case types.");
            throw e;
        }
    }

    //=============================================== other ============================================================

    @NotNull
    public static String encodedToString(String authorization) {
        return encodedToString(authorization.getBytes(StandardCharsets.UTF_8));
    }

    @NotNull
    public static String encodedToString(byte[] pArrayn) {
        return Base64.getEncoder().encodeToString(pArrayn);
    }

    @NotNull
    public static <T> T fromJson(@Nullable JsonElement json, @NotNull Class<T> classT) throws IOException {
        if (json == null) {
            throw new GtTJsonException("Unexpected empty response");
        }

        try {
            T res = gson.fromJson(json, classT);
            if (res == null) throw new GtTJsonException("Empty Json response");
            return res;
        }
        catch (ClassCastException | JsonParseException e) {
            throw new GtTJsonException("Parse exception while converting JSON to object " + classT.toString(), e);
        }
    }

    //================================ private methods =================================================================

    /**
     * выгрузить json объект, преобразовав его в type
     */
    @NotNull
    private static <T> T load(@NotNull TestRailConnection connection,
                              @NotNull String path,
                              @NotNull Class<? extends T> type,
                              @NotNull Header... headers) throws IOException {
        JsonElement result = connection.getRequest(path, headers);
        return fromJson(result, type);
    }

    /**
     * выгрузить json array, преобразовав его в type
     */
    @NotNull
    private static <T> List<T> loadAll(@NotNull TestRailConnection connection,
                                       @NotNull String path,
                                       @NotNull Class<? extends T[]> type,
                                       @NotNull Header... headers) throws IOException {
        PagedRequest<T> request = new ArrayPagedRequest<>(path, type, headers);
        return request.getAll(connection);
    }
}
