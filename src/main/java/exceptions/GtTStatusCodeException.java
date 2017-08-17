package exceptions;

import api.testrail.data.TestRailErrorMessage;
import org.jetbrains.annotations.Nullable;

/**
 * Created by v.nosov on 08.06.2017.
 */
public class GtTStatusCodeException extends GtTConfusingException {
    private final int myStatusCode;
    private final TestRailErrorMessage myError;

    public GtTStatusCodeException(String message, int statusCode) {
        this(message, null, statusCode);
    }

    public GtTStatusCodeException(String message, TestRailErrorMessage error, int statusCode) {
        super(message);
        myStatusCode = statusCode;
        myError = error;
    }

    public int getStatusCode() {
        return myStatusCode;
    }

    @Nullable
    public TestRailErrorMessage getError() {
        return myError;
    }
}
