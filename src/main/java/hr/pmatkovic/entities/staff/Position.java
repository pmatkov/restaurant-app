package hr.pmatkovic.entities.staff;

import hr.pmatkovic.exceptions.IllegalEnumArgumentException;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Represents an enumeration for categorizing employees positions
 */

public enum Position implements Serializable {

    GENERAL_MANAGER(1, "General manager"),
    BAR_MANAGER(2, "Bar manager"),
    FLOOR_MANAGER(3, "Floor manager"),
    KITCHEN_MANAGER(4, "Kitchen manager"),
    HEAD_BARTENDER(5, "Head bartender"),
    BARTENDER(6, "Bartender"),
    HEAD_COOK(7, "Head cook"),
    COOK(8, "Cook"),
    DISHWASHER(9, "Dishwasher"),
    HEAD_WAITER(10, "Head waiter"),
    WAITER(11, "Waiter");

    private final Integer numericPosition;
    private final String position;

    Position(Integer numericPosition, String position) {
        this.numericPosition = numericPosition;
        this.position = position;
    }

    public Integer getNumber() {
        return numericPosition;
    }

    @Override
    public String toString() {
        return position;
    }

    public static Position convertToEnum(Integer position) throws IllegalEnumArgumentException {

        return switch(position) {
            case 1 -> GENERAL_MANAGER;
            case 2 -> BAR_MANAGER;
            case 3 -> FLOOR_MANAGER;
            case 4 -> KITCHEN_MANAGER;
            case 5 -> HEAD_BARTENDER;
            case 6 -> BARTENDER;
            case 7 -> HEAD_COOK;
            case 8 -> COOK;
            case 9 -> DISHWASHER;
            case 10 -> HEAD_WAITER;
            case 11 -> WAITER;
            default -> throw new IllegalEnumArgumentException("Argument can't be converted to enum.");
        };
    }

    public static Position convertToEnum(String position) throws IllegalEnumArgumentException {

        if (position == null || position.isEmpty() || position.isBlank())
            return null;
        for (Position pos: values()){
            if (pos.toString().equalsIgnoreCase(position)) {
                return pos;
            }
        }
        throw new IllegalEnumArgumentException("Argument can't be converted to enum.");
    }

    public static List<String> getListOfStrings() {

        return Stream.of(Position.values())
                .map(Position::toString)
                .collect(Collectors.toList());
    }

    public static List<Position> getListOfEnumValues() {

        return Stream.of(Position.values()).collect(Collectors.toList());
    }

    public static List<Position> getListOfManagers() {

        return Stream.of(Position.values()).filter(Position::isManager).collect(Collectors.toList());
    }

    public static List<Position> getListOfEmployees() {

        return Stream.of(Position.values()).filter(Position::isRegularEmployee).collect(Collectors.toList());
    }

    public static Boolean isGeneralManager(Position position) {

        return position.equals(GENERAL_MANAGER);
    }

    public static Boolean isManager(Position position) {

        switch(position) {
            case BAR_MANAGER, FLOOR_MANAGER, KITCHEN_MANAGER -> {return true;}
            default -> {return false;}
        }
    }

    public static Boolean isRegularEmployee(Position position) {

        switch(position) {
            case HEAD_BARTENDER, BARTENDER, HEAD_COOK, COOK, DISHWASHER, HEAD_WAITER, WAITER -> {return true;}
            default -> {return false;}
        }
    }

    public static Boolean isPosition(String position) {

        for (Position pos : values()) {
            if (pos.toString().equalsIgnoreCase(position)) {
                return true;
            }
        }
        return false;
    }

}
