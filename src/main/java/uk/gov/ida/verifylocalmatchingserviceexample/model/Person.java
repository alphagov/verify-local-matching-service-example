package uk.gov.ida.verifylocalmatchingserviceexample.model;

import org.jdbi.v3.core.mapper.reflect.ColumnName;

import java.time.LocalDate;


public class Person {
    private String surname;
    @ColumnName("date_of_birth")
    private LocalDate dateOfBirth;

    //required by jdbi mapper
    public Person() {}

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

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
}
