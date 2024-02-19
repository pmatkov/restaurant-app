package hr.pmatkovic.exceptions;

import java.io.Serial;

/**
 * Represents an exception for deserialization problems
 */

public class DeserializationException extends Exception {

    @Serial
    private static final long serialVersionUID = -857496595952925792L;

    public DeserializationException() {
    }

    public DeserializationException(String message) {
        super(message);
    }

    public DeserializationException(String message, Throwable cause) {
        super(message, cause);
    }

    public DeserializationException(Throwable cause) {
        super(cause);
    }
}
