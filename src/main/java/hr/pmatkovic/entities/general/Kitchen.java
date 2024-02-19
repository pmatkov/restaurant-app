package hr.pmatkovic.entities.general;

import hr.pmatkovic.entities.staff.*;
import hr.pmatkovic.exceptions.DepartmentCreationException;

import java.util.List;

/**
 * Represents a kitchen within a restaurant
 */

public class Kitchen extends Department {

    public Kitchen(List<Employee> listOfEmployees) throws DepartmentCreationException {

        super(listOfEmployees, DepartmentName.KITCHEN);
    }

}
