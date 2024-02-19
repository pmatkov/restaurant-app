package hr.pmatkovic.javafx;

import java.util.Comparator;

/**
 * Compares numbers
 */

public class NumbersComparator implements Comparator<String> {

    @Override
    public int compare(String s1, String s2) {

        Double d1 = Double.parseDouble(s1);
        Double d2 = Double.parseDouble(s2);
        return d1.compareTo(d2);

    }
}
