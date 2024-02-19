package hr.pmatkovic.entities.general;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;


/**
 * Represents the base class for all persons
 */
public abstract class Person extends Identifier implements Serializable {

    @Serial
    private static final long serialVersionUID = -8732965523928128414L;
    private String name;
    private String surname;
    private Long pin;
    private LocalDate birthdate;

    public Person(Long ID, String name, String surname, Long pin, LocalDate birthdate) {
        super(ID);
        this.name = name;
        this.surname = surname;
        this.pin = pin;
        this.birthdate = birthdate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public Long getPin() {
        return pin;
    }

    public void setPin(Long pin) {
        this.pin = pin;
    }

    public LocalDate getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(LocalDate birthdate) {
        this.birthdate = birthdate;
    }

}
