package uk.gov.ida.verifylocalmatchingserviceexample.model;

import java.time.LocalDate;

public class Person {

    private Integer personId;
    private final String surname;
    private final LocalDate dateOfBirth;
    private final Address address;

    public Person(
        Integer personId,
        String surname,
        LocalDate dateOfBirth,
        Address address
    ) {
        this.personId = personId;
        this.surname = surname;
        this.dateOfBirth = dateOfBirth;
        this.address = address;
    }

    public String getSurname() {
        return surname;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public Address getAddress() {
        return address;
    }

    public Integer getPersonId() {
        return personId;
    }
}
