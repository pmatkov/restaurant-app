package hr.pmatkovic.entities.general;

import hr.pmatkovic.entities.staff.Position;

import java.io.Serializable;

/**
 * Represents an enumeration for categorizing department types within a restaurant
 */

public enum DepartmentName implements Serializable {

    ALL("All"),
    BAR("Bar"),
    FLOOR("Floor"),
    KITCHEN("Kitchen");

    private String name;

    DepartmentName(String name) {
        this.name = name;
    }

    public String getString() {
        return name;
    }

    public static DepartmentName convertPositionToDepartment(Position position) {

        if (position == null)
            return null;
        return switch (position) {
            case GENERAL_MANAGER -> ALL;
            case BAR_MANAGER, HEAD_BARTENDER, BARTENDER -> BAR;
            case FLOOR_MANAGER, HEAD_WAITER, WAITER -> FLOOR;
            case KITCHEN_MANAGER, HEAD_COOK, COOK, DISHWASHER -> KITCHEN;
        };

    }

    public static Position getDepartmentHead(DepartmentName departmentName) {

        if (departmentName == null)
            return null;
        return switch (departmentName) {
            case ALL -> Position.GENERAL_MANAGER;
            case BAR -> Position.BAR_MANAGER;
            case FLOOR -> Position.FLOOR_MANAGER;
            case KITCHEN -> Position.KITCHEN_MANAGER;
        };
    }
}
