package hr.pmatkovic.entities.staff;

import java.util.Comparator;

/**
 * Compares employees by surname
 */

public class EmployeesSorter implements Comparator<Employee> {

    @Override
    public int compare(Employee e1, Employee e2) {

        return e1.getSurname().compareTo(e2.getSurname());
    }
}
