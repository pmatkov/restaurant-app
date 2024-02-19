package hr.pmatkovic.entities.general;

import hr.pmatkovic.entities.staff.*;
import hr.pmatkovic.exceptions.DepartmentCreationException;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * Respresents a department within a restaurant
 */
public abstract class Department implements Performer {

    private Manager manager;
    private Set<Employee> departmentEmployees = new TreeSet<>(new EmployeesSorter());

    public Department(List<Employee> listOfEmployees, DepartmentName departmentName) throws DepartmentCreationException {
        this.manager = findHeadOfDepartment(listOfEmployees, departmentName);
        this.departmentEmployees = findEmployeesInDepartment(listOfEmployees, departmentName);
    }

    public Manager getManager() {
        return manager;
    }

    public void findHeadOfDepartment(Manager setManager) {
        this.manager = manager;
    }

    public Set<Employee> getDepartmentEmployees() {
        return departmentEmployees;
    }

    public void setDepartmentEmployees(Set<Employee> departmentEmployees) {
        this.departmentEmployees = departmentEmployees;
    }

    /**
     * Finds a department head
     * @param listOfEmployees
     * @param departmentName
     * @return department head
     * @throws DepartmentCreationException if an employee represents a department head
     */

    Manager findHeadOfDepartment(List<Employee> listOfEmployees, DepartmentName departmentName) throws DepartmentCreationException {

        Position position = DepartmentName.getDepartmentHead(departmentName);

        Optional<Employee> departmentHead = listOfEmployees.stream().filter(e -> e.getPosition().equals(position)).findFirst();

        if (departmentHead.isEmpty())
            throw new DepartmentCreationException();

        return (Manager)departmentHead.get();

    }

    /**
     * Finds employees within a department
     * @param listOfEmployees
     * @param departmentName
     * @return set of employees
     * @throws DepartmentCreationException if there are no employees within a department
     */

    Set<Employee> findEmployeesInDepartment(List<Employee> listOfEmployees, DepartmentName departmentName) throws DepartmentCreationException {

        Set<Employee> departmentEmployees = listOfEmployees.stream().filter(s -> s.getDepartment().equals(departmentName)).collect(Collectors.toSet());

        if (departmentEmployees.isEmpty())
            throw new DepartmentCreationException();

        return departmentEmployees;

    }

}

