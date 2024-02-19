package hr.pmatkovic.entities.service;

import hr.pmatkovic.exceptions.IllegalEnumArgumentException;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
/**
 * Represents an enumeration for categorizing reservation types
 */

public enum ReservationType implements Serializable {

    LOCAL(1, "local"),
    WEB(2, "web");

    private final Integer numericReservationType;
    private final String reservation;

    ReservationType(Integer numericReservationType, String reservation) {
        this.numericReservationType = numericReservationType;
        this.reservation = reservation;
    }

    public Integer getNumber() {
        return numericReservationType;
    }

    @Override
    public String toString() {
        return reservation;
    }

    public static ReservationType convertToEnum(Integer reservationType) throws IllegalEnumArgumentException {

        return switch(reservationType) {
            case 1 -> LOCAL;
            case 2 -> WEB;
            default -> throw new IllegalEnumArgumentException("Argument can't be converted to enum.");
        };
    }

    public static ReservationType convertToEnum(String reservationType) throws IllegalEnumArgumentException {

        if (reservationType == null || reservationType.isEmpty() || reservationType.isBlank())
            return null;
        for (ReservationType res: values()){
            if (res.toString().equalsIgnoreCase(reservationType)) {
                return res;
            }
        }
        throw new IllegalEnumArgumentException("Argument can't be converted to enum.");
    }

    public static List<String> getListOfStrings() {

        return Stream.of(ReservationType.values())
                .map(ReservationType::toString)
                .collect(Collectors.toList());
    }

    public static List<ReservationType> getListOfEnumValues() {

        return Stream.of(ReservationType.values()).collect(Collectors.toList());
    }

}
