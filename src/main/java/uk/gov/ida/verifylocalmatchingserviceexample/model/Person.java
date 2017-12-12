package uk.gov.ida.verifylocalmatchingserviceexample.model;

public class Person {
    private String surname;

    public Person() {}

    public Person(String surname) {
        this.surname = surname;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }
}
