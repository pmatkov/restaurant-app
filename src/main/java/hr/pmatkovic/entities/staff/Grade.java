package hr.pmatkovic.entities.staff;

import hr.pmatkovic.exceptions.IllegalEnumArgumentException;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Represents an enumeration for categorizing performance grades
 */

public enum Grade implements Serializable {

    EXCELLENT(5, "Excellent"),
    VERY_GOOD(4, "Very good"),
    GOOD(3, "Good"),
    SATISFACTORY(2, "Satisfactory"),
    POOR(1, "Poor");

    private final Integer numericGrade;
    private final String grade;

    Grade(Integer numericGrade, String grade) {
        this.numericGrade = numericGrade;
        this.grade = grade;
    }

    public Integer getNumber() {
        return numericGrade;
    }

    @Override
    public String toString() {
        return grade;
    }

    public static Grade convertToEnum(Integer grade) throws IllegalEnumArgumentException {

        return switch(grade) {
            case 5 -> EXCELLENT;
            case 4 -> VERY_GOOD;
            case 3 -> GOOD;
            case 2 -> SATISFACTORY;
            case 1 -> POOR;
            default -> throw new IllegalEnumArgumentException("Argument can't be converted to enum.");
        };
    }

    public static Grade convertToEnum(String grade) throws IllegalEnumArgumentException {

        if (grade == null || grade.isEmpty() || grade.isBlank())
            return null;
        for (Grade gr: values()){
            if (gr.toString().equalsIgnoreCase(grade)) {
                return gr;
            }
        }
        throw new IllegalEnumArgumentException("Argument can't be converted to enum.");
    }

    public static List<String> getListOfStrings() {

        return Stream.of(Grade.values())
                .map(Grade::toString)
                .collect(Collectors.toList());
    }

    public static List<Grade> getListOfEnumValues() {

        return Stream.of(Grade.values()).collect(Collectors.toList());

    }

}
