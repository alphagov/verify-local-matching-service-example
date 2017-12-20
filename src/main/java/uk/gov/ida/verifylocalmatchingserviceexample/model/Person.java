package uk.gov.ida.verifylocalmatchingserviceexample.model;

import java.time.LocalDate;

public class Person {
    private String surname;
    private LocalDate dateOfBirth;
    private Integer personId;

    public Person(String surname, LocalDate dateOfBirth, Integer personId) {
        this.surname = surname;
        this.dateOfBirth = dateOfBirth;
        this.personId = personId;
    }

    public String getSurname() {
        return surname;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public Integer getPersonId() {
        return personId;
    }
}
