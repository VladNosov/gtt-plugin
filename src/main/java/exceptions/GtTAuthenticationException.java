package exceptions;

import java.io.IOException;

/**
 * Created by v.nosov on 06.06.2017.
 */
public class GtTAuthenticationException extends IOException {
    public GtTAuthenticationException() {
        super();
    }

    public GtTAuthenticationException(String message) {
        super(message);
    }

    public GtTAuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }

    public GtTAuthenticationException(Throwable cause) {
        super(cause);
    }
}
