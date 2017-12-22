package uk.gov.ida.verifylocalmatchingserviceexample.builders;

import uk.gov.ida.verifylocalmatchingserviceexample.model.Address;
import uk.gov.ida.verifylocalmatchingserviceexample.model.Person;

import java.time.LocalDate;

import static uk.gov.ida.verifylocalmatchingserviceexample.builders.AddressBuilder.anyAddress;

public class PersonBuilder {

    private Integer id = 1111;
    private String surname = "default-surname";
    private LocalDate dateOfBirth = LocalDate.now().minusYears(20);
    private Address address = anyAddress();

    private PersonBuilder() {
        // prevents from using new
    }

    public static PersonBuilder aPerson() {
        return new PersonBuilder();
    }

    public static Person anyPerson() {
        return new PersonBuilder().build();
    }

    public Person build() {
        return new Person(id, surname, dateOfBirth, address);
    }

    public PersonBuilder withId(Integer id){
        this.id = id;
        return this;
    }

    public PersonBuilder withSurname(String surname) {
        this.surname = surname;
        return this;
    }

    public PersonBuilder withDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
        return this;
    }

    public PersonBuilder withAddress(Address address) {
        this.address = address;
        return this;
    }
}
