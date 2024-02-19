package hr.pmatkovic.javafx;

import hr.pmatkovic.entities.general.EntityMarker;
import hr.pmatkovic.entities.staff.Employee;
import hr.pmatkovic.entities.general.ChangedData;

import java.util.Comparator;
import java.util.Map;

/**
 * Compares historic data
 */

public class HistoricDataSorter<T extends EntityMarker, V extends Employee> implements Comparator<Map.Entry<T, ChangedData<T, V>>> {

    @Override
    public int compare(Map.Entry<T, ChangedData<T, V>> e1, Map.Entry<T, ChangedData<T, V>> e2) {

        return e2.getValue().getTimeOfChange().compareTo(e1.getValue().getTimeOfChange());
    }
}
