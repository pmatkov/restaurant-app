package hr.pmatkovic.utils;

import javafx.animation.KeyFrame;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * A utility class which contains loggers and string constants
 */

public class GeneralUtils {

    public static final Logger logger = LoggerFactory.getLogger("defLogger");
    public static final Logger dbLogger = LoggerFactory.getLogger("dbLogger");
    public static final Logger authLogger = LoggerFactory.getLogger("authLogger");

    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy.");
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy. HH:mm:ss");

    public static final String DB_TITLE = "Database error";
    public static final String DB_HEADER = "There was a problem accessing a database.";
    public static final String DB_TEXT = "Try again later or contact system administrator for assistance.";

    public static final String AUTHENTICATION_TITLE = "Authentication error";
    public static final String AUTHENTICATION_HEADER = "Authentication failed.";
    public static final String AUTHENTICATION_TEXT = "The entered username and/ or password is not valid.";

    public static final String INPUT_TITLE = "Invalid input";
    public static final String INPUT_HEADER = "These input errors were detected: ";
    public static final String INPUT_DATE = "The entered date is not in valid format.";
    public static final String INPUT_NO_CHANGE = "No changes have been made.";

    public static final String CONFIRM_TITLE = "Confirm action";
    public static final String CONFIRM_HEADER = "You are about to make changes to saved data.";
    public static final String CONFIRM_CHANGE = "Are you sure you want to change ";
    public static final String CONFIRM_DELETE = "Are you sure you want to delete ";

    public static final String HISTORY_TITLE = "Historical data";
    public static final String HISTORY_HEADER = "Unable to access historical data.";

    public static final String ADDED = "New entry added";
    public static final String CHANGED = "Entry changed";
    public static final String DELETED = "Entry deleted";

    public static final String UI_TITLE = "UI error";
    public static final String UI_HEADER = "There was a problem modifiying user interface.";

    public static KeyFrame setNewKeyFrame(Duration time, Runnable runnable) {

        return new KeyFrame(time, s ->  {

            Platform.runLater(runnable);
        });
    }


}
