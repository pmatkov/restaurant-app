package hr.pmatkovic.exceptions;

import java.io.Serial;

/**
 * Represents an exception for department creation problems
 */

public class DepartmentCreationException extends RuntimeException{

    @Serial
    private static final long serialVersionUID = 6925710902499542212L;

    public DepartmentCreationException() {
    }

    public DepartmentCreationException(String message) {
        super(message);
    }

    public DepartmentCreationException(String message, Throwable cause) {
        super(message, cause);
    }

    public DepartmentCreationException(Throwable cause) {
        super(cause);
    }
}
