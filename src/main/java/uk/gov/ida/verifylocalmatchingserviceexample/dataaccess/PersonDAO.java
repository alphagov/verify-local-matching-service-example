package uk.gov.ida.verifylocalmatchingserviceexample.dataaccess;

import org.jdbi.v3.core.Jdbi;
import uk.gov.ida.verifylocalmatchingserviceexample.model.Person;

import java.time.LocalDate;
import java.util.List;

public class PersonDAO {
    private Jdbi jdbi;

    public PersonDAO(Jdbi jdbi) {
        this.jdbi = jdbi;
    }

    public List<Person> getMatchingUsers(List<String> surnames, LocalDate dateOfBirth) {
        return jdbi.withHandle(handle ->
                handle.createQuery("select surname, date_of_birth from person where surname in (<surnames>) and date_of_birth = :dateOfBirth")
                        .bindList("surnames", surnames)
                        .bind("dateOfBirth", dateOfBirth)
                        .mapToBean(Person.class)
                        .list()
        );
    }
}
