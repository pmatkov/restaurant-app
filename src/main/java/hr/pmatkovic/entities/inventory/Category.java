package hr.pmatkovic.entities.inventory;

import hr.pmatkovic.exceptions.IllegalEnumArgumentException;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Represents an enumeration for categorizing various types of food
 */

public enum Category {

    SOFT_DRINK(1, "Soft drink"),
    WINE(2, "Wine"),
    BEER(3, "Beer"),
    MEAT(4, "Meat"),
    FISH(5, "Fish"),
    VEGETABLES(6, "Vegetables"),
    FRUIT(7, "Fruit");

    private final Integer numericCategory;
    private final String category;

    Category(Integer numericCategory, String category) {
        this.numericCategory = numericCategory;
        this.category = category;
    }

    public Integer getNumber() {
        return numericCategory;
    }

    @Override
    public String toString() {
        return category;
    }

    public static Category convertToEnum(Integer category) throws IllegalEnumArgumentException {

        return switch(category) {
            case 1 -> SOFT_DRINK;
            case 2 -> WINE;
            case 3 -> BEER;
            case 4 -> MEAT;
            case 5 -> FISH;
            case 6 -> VEGETABLES;
            case 7 -> FRUIT;

            default -> throw new IllegalEnumArgumentException("Argument can't be converted to enum.");
        };
    }

    public static Category convertToEnum(String category) throws IllegalEnumArgumentException {

        if (category == null || category.isEmpty() || category.isBlank())
            return null;
        for (Category cat: values()){
            if (cat.toString().equalsIgnoreCase(category)) {
                return cat;
            }
        }
        throw new IllegalEnumArgumentException("Argument can't be converted to enum.");
    }

    public static List<Category> getListOfEnumValues() {

        return Stream.of(Category.values()).collect(Collectors.toList());
    }

    public static Boolean isDrink(Category category) {

        switch(category) {
            case SOFT_DRINK, WINE, BEER -> {return true;}
            default -> {return false;}
        }
    }

    public static Boolean isFood(Category category) {

        switch(category) {
            case MEAT, FISH, VEGETABLES, FRUIT -> {return true;}
            default -> {return false;}
        }
    }

}
