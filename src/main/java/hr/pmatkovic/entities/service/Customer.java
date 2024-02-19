package hr.pmatkovic.entities.service;

import hr.pmatkovic.entities.general.Person;

import java.io.Serial;
import java.time.LocalDate;

/**
 * Represents a customer
 */

public class Customer extends Person {

    @Serial
    private static final long serialVersionUID = 3341900866525455996L;
    private String email;
    private Boolean regularCustomer;
    private Boolean vipCustomer;

    public Customer(Long id, String name, String surname, String email, Boolean regularCustomer, Boolean vipCustomer) {
        this (id, name, surname, 0L, LocalDate.of(2000, 1, 1), email, regularCustomer, vipCustomer);
    }

    public Customer(Long id, String name, String surname, Long pin, LocalDate birthdate, Boolean regularCustomer, Boolean vipCustomer) {
        this (id, name, surname, pin, birthdate, "customer@test.com", regularCustomer, vipCustomer);
    }

    public Customer(Long id, String name, String surname, Long pin, LocalDate birthdate, String email, Boolean regularCustomer, Boolean vipCustomer) {
        super(id, name, surname, pin, birthdate);
        this.email = email;
        this.regularCustomer = regularCustomer;
        this.vipCustomer = vipCustomer;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getRegularCustomer() {
        return regularCustomer;
    }

    public void setRegularCustomer(Boolean regularCustomer) {
        this.regularCustomer = regularCustomer;
    }

    public Boolean getVipCustomer() {
        return vipCustomer;
    }

    public void setVipCustomer(Boolean vipCustomer) {
        this.vipCustomer = vipCustomer;
    }

    @Override
    public String toString() {
        return this.getName() + " " + this.getSurname();
    }
}
