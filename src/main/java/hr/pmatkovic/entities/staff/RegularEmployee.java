package hr.pmatkovic.entities.staff;

import hr.pmatkovic.entities.general.DepartmentName;
import hr.pmatkovic.entities.general.Evaluator;

import java.io.Serial;
import java.time.LocalDate;
import java.util.Optional;

/**
 * Represents a regular employee
 */

public final class RegularEmployee extends Employee implements Evaluator {

    @Serial
    private static final long serialVersionUID = 4892847305226179898L;

    public RegularEmployee(Long ID, String name, String surname, Long pin, LocalDate birthdate, DepartmentName departmentName, Position position, Rank rank, Double salary, Integer experience) {
        this(ID, name, surname, pin, birthdate, departmentName, position, rank, salary, experience, 0.0);

    }

    public RegularEmployee(Long ID, String name, String surname, Long pin, LocalDate birthdate, DepartmentName departmentName, Position position, Rank rank, Double salary, Integer experience, Double performance) {
        super(ID, name, surname, pin, birthdate, departmentName, position, rank, salary, experience, performance);

    }

    @Override
    public Optional<Double> calculateBonus (Employee employee) {

        if (Evaluator.isEligibleForBonus(employee) && employee.getPerformanceReviewed() && employee.getPerformance() > 3) {

            return Optional.of(employee.getPerformance().intValue() * 0.2 + employee.getExperience() * 0.3 + employee.getRank().getBonusPerRank().doubleValue());
        }
        return Optional.empty();
    }


}
