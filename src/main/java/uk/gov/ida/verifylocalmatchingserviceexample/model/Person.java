package uk.gov.ida.verifylocalmatchingserviceexample.model;

import java.time.LocalDate;

public class Person {

    private Integer personId;
    private final String surname;
    private final String firstName;
    private final LocalDate dateOfBirth;
    private final Address address;

    public Person(
        Integer personId,
        String surname,
        String firstName, LocalDate dateOfBirth,
        Address address
    ) {
        this.personId = personId;
        this.surname = surname;
        this.firstName = firstName;
        this.dateOfBirth = dateOfBirth;
        this.address = address;
    }

    public String getSurname() {
        return surname;
    }

    public String getFirstName() {
        return firstName;
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
