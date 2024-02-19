package hr.pmatkovic.exceptions;

import java.io.Serial;

/**
 * Represents an exception for invalid enum arguments
 */

public class IllegalEnumArgumentException extends RuntimeException{

    @Serial
    private static final long serialVersionUID = -8840302582358192503L;

    public IllegalEnumArgumentException() {
    }

    public IllegalEnumArgumentException(String message) {
        super(message);
    }

    public IllegalEnumArgumentException(String message, Throwable cause) {
        super(message, cause);
    }

    public IllegalEnumArgumentException(Throwable cause) {
        super(cause);
    }

}
