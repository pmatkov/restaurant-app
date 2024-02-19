package hr.pmatkovic.spring.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="reservation_type")
    private String reservationType;
    @Column(name="guests")
    private Integer numberOfGuests;
    @Column(name="date_time")
    private LocalDateTime reservationDateTime;

    @OneToOne
    @JoinColumn(name="customer_id")
    private Customer customer;

    public Reservation() {};

    public Reservation(ReservationRequest request) {
        this.reservationType = request.getReservationType();
        this.numberOfGuests = request.getNumberOfGuests();
        this.reservationDateTime = request.getReservationDateTime();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReservationType() {
        return reservationType;
    }

    public void setReservationType(String reservationType) {
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

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
}
