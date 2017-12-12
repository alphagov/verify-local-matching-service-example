package uk.gov.ida.verifylocalmatchingserviceexample.dataaccess;

import org.jdbi.v3.core.Jdbi;
import uk.gov.ida.verifylocalmatchingserviceexample.model.Person;

import java.util.List;

public class PersonDAO {
    private Jdbi jdbi;

    public PersonDAO(Jdbi jdbi) {
        this.jdbi = jdbi;
    }

    public List<Person> getMatchingUsers(List<String> surnames) {
        return jdbi.withHandle(handle ->
                handle.createQuery("select surname from person where surname in (<surnames>)")
                        .bindList("surnames", surnames)
                        .mapToBean(Person.class)
                        .list()
        );
    }
}
