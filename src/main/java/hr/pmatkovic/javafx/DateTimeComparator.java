package hr.pmatkovic.javafx;

import java.time.LocalDateTime;
import java.util.Comparator;

import static hr.pmatkovic.utils.GeneralUtils.DATE_TIME_FORMATTER;

/**
 * Compares dates and times
 */

public class DateTimeComparator implements Comparator<String> {

    @Override
    public int compare(String s1, String s2) {

        LocalDateTime dt1 = LocalDateTime.parse(s1, DATE_TIME_FORMATTER);
        LocalDateTime dt2 = LocalDateTime.parse(s2, DATE_TIME_FORMATTER);

        return dt1.compareTo(dt2);
    }
}
