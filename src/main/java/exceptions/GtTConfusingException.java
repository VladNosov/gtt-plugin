package exceptions;

import org.jetbrains.annotations.Nullable;
import java.io.IOException;

/**
 * Created by v.nosov on 08.06.2017.
 */
public class GtTConfusingException extends IOException{
    private String myDetails;

    public GtTConfusingException() {
    }

    public GtTConfusingException(String message) {
        super(message);
    }

    public GtTConfusingException(String message, Throwable cause) {
        super(message, cause);
    }

    public GtTConfusingException(Throwable cause) {
        super(cause);
    }

    public void setDetails(@Nullable String details) {
        myDetails = details;
    }

    @Override
    public String getMessage() {
        if (myDetails == null) {
            return super.getMessage();
        }
        else {
            return myDetails + "\n\n" + super.getMessage();
        }
    }
}