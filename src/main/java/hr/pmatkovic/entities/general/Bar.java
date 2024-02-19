package hr.pmatkovic.entities.general;

import hr.pmatkovic.entities.staff.Employee;
import hr.pmatkovic.entities.staff.Rank;
import hr.pmatkovic.exceptions.DepartmentCreationException;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Represents a bar within a restaurant
 */

public class Bar extends Department {

    public Bar(List<Employee> listOfEmployees) throws DepartmentCreationException {

        super(listOfEmployees, DepartmentName.BAR);
    }

    /**
     * Finds the best performing employee within the department who is younger than 25
     * @param employees
     * @return best performing employee
     */
    @Override
    public Optional<Employee> findBestPerformingEmployee(List<Employee> employees) {

        Predicate<Employee> lessThanTwentyFive = e -> e.getBirthdate().compareTo(LocalDate.now().minusYears(25)) > 1;

        return employees.stream().filter(e -> Rank.isRegularEmployee(e.getRank()))
                .filter(Employee::getPerformanceReviewed)
                .filter(lessThanTwentyFive)
                .max(Comparator.comparing(Employee::getPerformance));
    }
}
