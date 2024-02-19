package hr.pmatkovic.entities.general;

import hr.pmatkovic.entities.staff.Employee;
import hr.pmatkovic.entities.staff.Manager;
import hr.pmatkovic.entities.staff.Rank;
import hr.pmatkovic.entities.staff.RegularEmployee;

import java.util.Optional;

/**
 * This interface includes evaulotion methods
 */
public sealed interface Evaluator permits RegularEmployee, Manager {

    /**
     * Checks employee's bonus eligibility
     * @param employee
     * @return true if the employee's rank is that of a manager or higher than that of a trainee
     */

    static Boolean isEligibleForBonus(Employee employee) {

        if (employee instanceof Manager)
            return true;
        else if (employee instanceof RegularEmployee)
        {
            return employee.getRank().compareTo(Rank.TRAINEE) > 0;
        }
        else
            return false;
    }

    /**
     * Abstract method that calculates employee's bonus
     * @param employee
     * @return bonus
     */
    Optional<Double> calculateBonus(Employee employee);


}
