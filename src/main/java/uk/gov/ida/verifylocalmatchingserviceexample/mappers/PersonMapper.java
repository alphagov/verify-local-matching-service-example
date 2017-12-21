package uk.gov.ida.verifylocalmatchingserviceexample.mappers;

import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;
import uk.gov.ida.verifylocalmatchingserviceexample.model.Person;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class PersonMapper implements RowMapper<Person> {
    @Override
    public Person map(ResultSet rs, StatementContext ctx) throws SQLException {
        String surname = rs.getString("surname");
        LocalDate dateOfBirth = LocalDate.parse(rs.getString("date_of_birth"));
        Integer personId = rs.getInt("person_id");
        return new Person(surname, dateOfBirth, personId);
    }
}
