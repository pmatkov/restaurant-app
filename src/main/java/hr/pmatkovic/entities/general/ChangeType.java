package hr.pmatkovic.entities.general;

import hr.pmatkovic.exceptions.IllegalEnumArgumentException;

import java.io.Serializable;

/**
 * Represents an enumeration for categorizing administrative changes
 */

public enum ChangeType implements Serializable {

    ADD(1, "Added"),
    EDIT(2, "Edited"),
    DELETE(3, "Deleted");

    private final Integer numericChange;
    private final String change;

    ChangeType(Integer numericChange, String change) {
        this.numericChange = numericChange;
        this.change = change;
    }

    public Integer getNumber() {
        return numericChange;
    }

    public String getString() {
        return change;
    }

    public static ChangeType convertToEnum(Integer change) throws IllegalEnumArgumentException {

        return switch(change) {
            case 1 -> ADD;
            case 2 -> EDIT;
            case 3 -> DELETE;
            default -> throw new IllegalEnumArgumentException("Argument can't be converted to enum.");
        };
    }

    public static ChangeType convertToEnum(String change) throws IllegalEnumArgumentException {

        for (ChangeType ch: values()){
            if (ch.getString().equalsIgnoreCase(change)) {
                return ch;
            }
        }
        throw new IllegalEnumArgumentException("Argument can't be converted to enum.");
    }
}
