package project.actions.utils;

import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nullable;

/**
 * Created by v.nosov on 05.06.2017.
 */
public class GtTActionsUtil {

    private GtTActionsUtil() {
    }

    public static boolean hasProject(DataContext context) {
        return CommonDataKeys.PROJECT.getData(context) != null;
    }

    @Nullable
    public static Project getProject(DataContext context) {
        return CommonDataKeys.PROJECT.getData(context);
    }
}
