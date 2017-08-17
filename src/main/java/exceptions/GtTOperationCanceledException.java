package exceptions;

import java.io.IOException;

/**
 * Created by v.nosov on 06.06.2017.
 */
public class GtTOperationCanceledException extends IOException {
    public GtTOperationCanceledException() {
        super();
    }

    public GtTOperationCanceledException(String message) {
        super(message);
    }

    public GtTOperationCanceledException(String message, Throwable cause) {
        super(message, cause);
    }

    public GtTOperationCanceledException(Throwable cause) {
        super(cause);
    }
}
