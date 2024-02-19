package hr.pmatkovic.entities.general;

import hr.pmatkovic.entities.general.Department;
import hr.pmatkovic.entities.staff.Employee;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Represents interface for evaluation of employee's performance
 */
public interface Performer {
    /**
     * Finds best performing employee. Only overall performance is considered.
     * @param employees
     * @return best performing employee
     */
    default Optional<Employee> findBestPerformingEmployee(List<Employee> employees) {

        return employees.stream().filter(Employee::getPerformanceReviewed).max(Comparator.comparing(Employee::getPerformance));
    }
}
