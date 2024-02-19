package hr.pmatkovic.utils;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.*;

/**
 * A utility class for validating input
 */


public class ValidationUtils {

    private static Boolean isValidDate = true;

    public static Boolean getDateValidationStatus(){

        return isValidDate;
    }

    public static void setDateValidationStatus(Boolean status) {
        isValidDate = status;
    }


    public static Boolean isEmpty(Object input) {

        if (input instanceof String)
            return ((String)input).isEmpty() || ((String)input).isBlank();
        else if (input instanceof Optional) {
            return ((Optional<?>) input).isEmpty() && isValidDate;
        }

        return true;
    }

    public static <T> Optional<String> checkIfEmptyInput(Map<String, T> dataMap) {

        StringBuilder errors = new StringBuilder();
        int numberOfErrors = 0;

        for (String key : dataMap.keySet()) {

            T value = dataMap.get(key);

            if (isEmpty(value)) {
                errors.append(key).append(", ");
                numberOfErrors++;
            }
        }
        if (numberOfErrors > 0) {
            errors.deleteCharAt(errors.length() - 2).append(numberOfErrors > 1 ? "are required data." : "is required data.");
            return Optional.of(errors.toString());
        }

        return Optional.empty();
    }

    public static<T extends Number> T validateNumber(String number, Class<T> numberClass) {

        number = number.contains(",") ? number.replace(",", ".") : number;
        try {
            return (T) switch (numberClass.getSimpleName()) {
                case "Long" -> Long.valueOf(number);
                case "Integer" -> Integer.valueOf(number);
                case "Double" -> Double.valueOf(number);
                default -> null;
            };
        }
        catch (NumberFormatException | NullPointerException e) {
            return null;
        }
    }

    public static <T> Boolean isNumeric(String input) {

        if (input.contains(","))
            input = input.replace(",", ".");

        try {
            NumberFormat nf = NumberFormat.getInstance();
            nf.parse(input);

        } catch (NumberFormatException | ParseException e) {
            return false;
        }
        return true;
    }

    public static Optional<String> checkIfNumericInput(Map<String, String> dataMap) {

        StringBuilder errors = new StringBuilder();
        int numberOfErrors = 0;

        for (String key : dataMap.keySet()) {
            String value = dataMap.get(key);

            if (!isEmpty(value) && !isNumeric(value)) {
                errors.append(key).append(", ");
                numberOfErrors++;
            }
        }
        if (numberOfErrors > 0) {

            errors.deleteCharAt(errors.length() - 2).append(numberOfErrors > 1 ? "have to be entered" : "has to be entered").append(" in a numeric format.");
            return Optional.of(errors.toString());
        }
        return Optional.empty();
    }


    public static boolean isEveryObjectNull(Object... objects) {
        return Arrays.stream(objects).allMatch(Objects::isNull);
    }

}
