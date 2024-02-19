package hr.pmatkovic.entities.general;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a wrapper for departments within a restaurant
 * @param <T> a list of departments
 */

public class Organisation<T extends Department> implements Performer {

    private List<T> listOfDepartments;

    public Organisation(List<T> listOfDepartments) {
        this.listOfDepartments = listOfDepartments;
    }

    public Organisation(T... departments) {
        listOfDepartments = new ArrayList<>();

        for (T dep: departments) {
            addDepartment(dep);
        }
    }


    public void addDepartment(T department) {

        listOfDepartments.add(department);
    }

    public T getDepartment(int index) {

        return listOfDepartments.get(index);
    }


    public List<T> getDepartments() {
        return listOfDepartments;
    }

}
