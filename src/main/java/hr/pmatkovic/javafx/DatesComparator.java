package hr.pmatkovic.javafx;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;

import static hr.pmatkovic.utils.GeneralUtils.DATE_FORMATTER;

/**
 * Compares dates
 */

public class DatesComparator implements Comparator<String> {

    @Override
    public int compare(String s1, String s2) {

        LocalDate d1 = LocalDate.parse(s1, DATE_FORMATTER);
        LocalDate d2 = LocalDate.parse(s2, DATE_FORMATTER);

        return d1.compareTo(d2);
    }
}
