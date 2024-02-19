package hr.pmatkovic.entities.general;

import hr.pmatkovic.entities.staff.Employee;
import hr.pmatkovic.entities.staff.Rank;
import hr.pmatkovic.exceptions.DepartmentCreationException;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Represents a floor within a restaurant
 */

public class Floor extends Department {

    public Floor(List<Employee> listOfEmployees) throws DepartmentCreationException {

        super(listOfEmployees, DepartmentName.FLOOR);
    }

    /**
     * Finds the best performing employee. If more employees have the same performance, the youngest one has the advantage.
     * @param employees
     * @return best performing employee
     */
    @Override
    public Optional<Employee> findBestPerformingEmployee(List<Employee> employees) {

        return employees.stream()
                .filter(e -> Rank.isManager(e.getRank()))
                .filter(Employee::getPerformanceReviewed)
                .max(Comparator.comparing(Employee::getPerformance).thenComparing(Employee::getBirthdate));
    }
}
