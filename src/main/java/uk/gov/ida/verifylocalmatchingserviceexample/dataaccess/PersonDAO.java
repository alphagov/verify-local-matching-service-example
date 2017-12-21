package uk.gov.ida.verifylocalmatchingserviceexample.dataaccess;

import org.jdbi.v3.core.Jdbi;
import uk.gov.ida.verifylocalmatchingserviceexample.mappers.PersonMapper;
import uk.gov.ida.verifylocalmatchingserviceexample.model.Person;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class PersonDAO {
    private Jdbi jdbi;

    public PersonDAO(Jdbi jdbi) {
        this.jdbi = jdbi;
    }

    public List<Person> getMatchingUsers(List<String> surnames, LocalDate dateOfBirth) {
        return jdbi.withHandle(handle ->
                handle.createQuery("select person_id, surname, date_of_birth from person where LOWER(surname) in (<surnames>) and date_of_birth = :dateOfBirth")
                        .bindList("surnames", toLowerCase(surnames))
                        .bind("dateOfBirth", dateOfBirth)
                        .map(new PersonMapper())
                        .list()
        );
    }

    public boolean tableExists() {
        return jdbi.withHandle(handle -> handle.createQuery("select 1 from person")
            .mapTo(Integer.class)
            .findFirst()
            .isPresent()
        );
    }

    private List<String> toLowerCase(List<String> surnames) {
        return surnames.stream()
                .map(String::toLowerCase)
                .collect(Collectors.toList());
    }

}
