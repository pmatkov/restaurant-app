package hr.pmatkovic.entities.staff;

import hr.pmatkovic.entities.general.DepartmentName;
import hr.pmatkovic.entities.general.Evaluator;

import java.io.Serial;
import java.time.LocalDate;
import java.util.Optional;

/**
 * Represents a manager
 */

public final class Manager extends Employee implements Evaluator {

    @Serial
    private static final long serialVersionUID = 5298668718091577542L;


    public Manager(Long ID, String name, String surname, Long pin, LocalDate birthdate, DepartmentName departmentName, Position position, Rank rank, Double salary, Integer experience) {
        this(ID, name, surname, pin, birthdate, departmentName, position, rank, salary, experience, 0.0);
    }

    public Manager(Long ID, String name, String surname, Long pin, LocalDate birthdate, DepartmentName departmentName, Position position, Rank rank, Double salary, Integer experience, Double performance) {
        super(ID, name, surname, pin, birthdate, departmentName, position, rank, salary, experience, performance);
    }


    @Override
    public Optional<Double> calculateBonus (Employee employee) {

        if (Evaluator.isEligibleForBonus(employee) ) {

            return Optional.of(employee.getExperience() + employee.getRank().getBonusPerRank().doubleValue());

        }
        return Optional.empty();
    }

}
