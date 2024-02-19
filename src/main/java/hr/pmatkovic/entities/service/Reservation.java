package hr.pmatkovic.entities.service;

import hr.pmatkovic.entities.general.EntityMarker;
import hr.pmatkovic.entities.general.Identifier;

import java.io.Serial;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Represents a reservation
 */

public class Reservation extends Identifier implements EntityMarker {

    @Serial
    private static final long serialVersionUID = 6781101139058035210L;

    private Customer customer;
    private ReservationType reservationType;
    private Integer numberOfGuests;
    private LocalDateTime reservationDateTime;

    public Reservation(Long id, Customer customer, Integer numberOfGuests, LocalDateTime reservationDateTime) {
        this(id, customer, ReservationType.LOCAL, numberOfGuests, reservationDateTime);
    }

    public Reservation(Long id, Customer customer, ReservationType reservationType, Integer numberOfGuests, LocalDateTime reservationDateTime) {
        super(id);
        this.reservationType = reservationType;
        this.customer = customer;
        this.numberOfGuests = numberOfGuests;
        this.reservationDateTime = reservationDateTime;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public ReservationType getReservationType() {
        return reservationType;
    }

    public void setReservationType(ReservationType reservationType) {
        this.reservationType = reservationType;
    }

    public Integer getNumberOfGuests() {
        return numberOfGuests;
    }

    public void setNumberOfGuests(Integer numberOfGuests) {
        this.numberOfGuests = numberOfGuests;
    }

    public LocalDateTime getReservationDateTime() {
        return reservationDateTime;
    }

    public void setReservationDateTime(LocalDateTime reservationDateTime) {
        this.reservationDateTime = reservationDateTime;
    }


}
