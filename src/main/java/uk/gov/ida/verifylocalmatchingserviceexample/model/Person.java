package uk.gov.ida.verifylocalmatchingserviceexample.model;

import java.time.LocalDate;


public class Person {
    private String surname;
    private LocalDate dateOfBirth;

    public Person(String surname, LocalDate dateOfBirth) {
        this.surname = surname;
        this.dateOfBirth = dateOfBirth;
    }

    public String getSurname() {
        return surname;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }
}
