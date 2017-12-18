package uk.gov.ida.verifylocalmatchingserviceexample.dataaccess;

import org.jdbi.v3.core.Jdbi;
import uk.gov.ida.verifylocalmatchingserviceexample.mappers.PersonMapper;
import uk.gov.ida.verifylocalmatchingserviceexample.model.Person;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
                .bind("dateOfBirth", dateOfBirth.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
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
}
