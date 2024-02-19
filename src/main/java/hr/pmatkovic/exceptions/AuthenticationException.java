package hr.pmatkovic.exceptions;

import java.io.Serial;

/**
 * Represents an exception for authentication problems
 */

public class AuthenticationException extends Exception {

    @Serial
    private static final long serialVersionUID = -3546128918621947404L;

    public AuthenticationException() {
    }

    public AuthenticationException(String message) {
        super(message);
    }

    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }

    public AuthenticationException(Throwable cause) {
        super(cause);
    }
}
