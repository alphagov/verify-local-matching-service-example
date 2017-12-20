package uk.gov.ida.verifylocalmatchingserviceexample.dataaccess;

import org.jdbi.v3.core.Jdbi;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.assertTrue;

public class VerifiedPidDAOTest {
    @Rule
    public TestDataSourceRule testDataSourceRule = new TestDataSourceRule();
    private Jdbi jdbi;

    @Before
    public void setUp() {
        jdbi = testDataSourceRule.getJdbi();
    }

    @Test
    public void shouldSaveVerifiedPidIntoTheDatabase() {
        VerifiedPidDAO verifiedPidDAO = new VerifiedPidDAO(jdbi);

        int personId = 22345;
        createPersonWithAddress(personId);

        String pid = "random pid";
        verifiedPidDAO.save(pid, personId);

        assertTrue(getVerifiedPidFromDatabase(pid).isPresent());
    }

    private Optional<String> getVerifiedPidFromDatabase(String pid) {
        return jdbi.withHandle(handle ->
                handle.select("select pid from verifiedPid where pid = ?", pid)
                        .mapTo(String.class)
                        .findFirst());
    }

    private void createPersonWithAddress(int person_id) {
        jdbi.useHandle(handle -> {
            handle.execute("insert into address (postcode) values ('CT15 1ZZ')");
            handle.execute("insert into person (person_id, address) values " +
                    "('" + person_id + "', (SELECT address_id FROM address WHERE postcode = 'CT15 1ZZ'))");
        });
    }
}