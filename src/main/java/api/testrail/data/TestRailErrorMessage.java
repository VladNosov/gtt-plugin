package api.testrail.data;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.io.mandatory.RestModel;
import java.util.List;

/**
 * Класс, содержащий инфу об ошибке
 * Created by v.nosov on 08.06.2017.
 */
@SuppressWarnings("UnusedDeclaration")
public class TestRailErrorMessage {
    private String message;
    private List<Error> errors;

    @RestModel
    public static class Error {
        private String resource;
        private String field;
        private String code;
        private String message;
    }

    @Nullable
    public String getMessage() {
        if (errors == null) {
            return message;
        }
        else {
            StringBuilder s = new StringBuilder();
            s.append(message);
            for (Error e : errors) {
                s.append(String.format("<br/>[%s; %s]%s: %s", e.resource, e.field, e.code, e.message));
            }
            return s.toString();
        }
    }

    public boolean containsReasonMessage(@NotNull String reason) {
        if (message == null) return false;
        return message.contains(reason);
    }

    public boolean containsErrorCode(@NotNull String code) {
        if (errors == null) return false;
        for (Error error : errors) {
            if (error.code != null && error.code.contains(code)) return true;
        }
        return false;
    }

    public boolean containsErrorMessage(@NotNull String message) {
        if (errors == null) return false;
        for (Error error : errors) {
            if (error.code != null && error.code.contains(message)) return true;
        }
        return false;
    }
}