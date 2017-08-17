package exceptions;

/**
 * Created by v.nosov on 08.06.2017.
 */
public class GtTJsonException extends GtTConfusingException {
    public GtTJsonException() {
        super();
    }

    public GtTJsonException(String message) {
        super(message);
    }

    public GtTJsonException(String message, Throwable cause) {
        super(message, cause);
    }

    public GtTJsonException(Throwable cause) {
        super(cause);
    }
}