package hr.pmatkovic.entities.staff;

import hr.pmatkovic.exceptions.IllegalEnumArgumentException;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Represents an enumeration for categorizing employees ranks
 */

public enum Rank implements Serializable {

    GENERAL_MANAGER(1, 10,"General manager"),
    MANAGER(2, 5,"Manager"),
    SENIOR(3, 2,"Senior"),
    JUNIOR(4, 1,"Junior"),
    TRAINEE(5, 0,"Trainee");

    private final Integer numericRank;
    private final String rank;
    private final Integer bonusPerRank;

    Rank(Integer numericRank, Integer bonusPerRank, String rank) {
        this.numericRank = numericRank;
        this.rank = rank;
        this.bonusPerRank = bonusPerRank;
    }

    public Integer getNumber() {
        return numericRank;
    }

    @Override
    public String toString() {
        return rank;
    }

    public Integer getBonusPerRank() {
        return bonusPerRank;
    }

    public static Rank convertToEnum(Integer rank) throws IllegalEnumArgumentException {

        return switch (rank) {
            case 1 -> GENERAL_MANAGER;
            case 2 -> MANAGER;
            case 3 -> SENIOR;
            case 4 -> JUNIOR;
            case 5 -> TRAINEE;
            default ->  throw new IllegalEnumArgumentException("Argument can't be converted to enum.");
        };
    }

    public static Rank convertToEnum(Position position, Integer workExperience) throws IllegalEnumArgumentException {

        switch (position) {
            case GENERAL_MANAGER -> {return GENERAL_MANAGER;}
            case BAR_MANAGER, FLOOR_MANAGER, KITCHEN_MANAGER -> {return MANAGER;}
            case HEAD_BARTENDER, BARTENDER, HEAD_COOK, COOK, DISHWASHER, HEAD_WAITER, WAITER -> {
                if (workExperience > 4)
                    return SENIOR;
                else if (workExperience > 2)
                    return JUNIOR;
                else
                    return TRAINEE;
            }
            default -> throw new IllegalEnumArgumentException("Argument can't be converted to enum.");
        }

    }

    public static Rank convertToEnum(String rank) throws IllegalEnumArgumentException {

        if (rank == null || rank.isEmpty() || rank.isBlank())
            return null;
        for (Rank r: values()){
            if (r.toString().equalsIgnoreCase(rank)) {
                return r;
            }
        }
        throw new IllegalEnumArgumentException("Argument can't be converted to enum.");
    }

    public static List<String> getListOfStrings() {

        return Stream.of(Rank.values())
                .map(Rank::toString)
                .collect(Collectors.toList());
    }


    public static List<Rank> getListOfEnumValues() {

        return Stream.of(Rank.values()).collect(Collectors.toList());
    }

    public static List<Rank> getListOfEmployees() {

        return Stream.of(Rank.values()).filter(Rank::isRegularEmployee).collect(Collectors.toList());
    }

    public static Boolean isGeneralManager(Rank rank) {

        return rank.equals(GENERAL_MANAGER);
    }

    public static Boolean isManager(Rank rank) {

        return rank.equals(MANAGER);
    }

    public static Boolean isRegularEmployee(Rank rank) {

        switch(rank) {
            case TRAINEE, JUNIOR, SENIOR -> {return true;}
            default -> {return false;}
        }
    }
}
