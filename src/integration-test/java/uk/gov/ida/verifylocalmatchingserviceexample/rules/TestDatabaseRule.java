package uk.gov.ida.verifylocalmatchingserviceexample.rules;

import io.dropwizard.testing.junit.DropwizardAppRule;
import org.flywaydb.core.Flyway;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;
import org.junit.rules.ExternalResource;
import uk.gov.ida.verifylocalmatchingserviceexample.configuration.VerifyLocalMatchingServiceExampleConfiguration;

import java.time.LocalDate;

public class TestDatabaseRule extends ExternalResource {

    private Handle handle;
    private DropwizardAppRule<VerifyLocalMatchingServiceExampleConfiguration> appRule;

    public TestDatabaseRule(DropwizardAppRule<VerifyLocalMatchingServiceExampleConfiguration> appRule) {
        this.appRule = appRule;
    }

    @Override
    protected void before() {
        handle = Jdbi.create(this.appRule.getConfiguration().getDatabaseConfiguration().getUrl()).open();
        handle.begin();
        setUpDatabase();
        handle.commit();
    }

    @Override
    protected void after() {
        handle.close();
    }

    private void setUpDatabase() {
        Flyway flyway = new Flyway();
        flyway.setDataSource(this.appRule.getConfiguration().getDatabaseConfiguration().getUrl(),
            this.appRule.getConfiguration().getDatabaseConfiguration().getUserName(),
            this.appRule.getConfiguration().getDatabaseConfiguration().getPassword());
        flyway.setLocations("classpath:db.migration");
        flyway.clean();
        flyway.migrate();
    }

    public void ensurePidExist(String verifiedPid) {
        ensurePidDoesNotExist(verifiedPid);
        handle.createUpdate("insert into verifiedPid (pid, person) values(:verifiedPid, (select person_id from person limit 1))")
            .bind("verifiedPid", verifiedPid)
            .execute();
    }

    public void ensurePidDoesNotExist(String verifiedPid) {
        handle.createUpdate("delete from verifiedPid where pid = :verifiedPid")
            .bind("verifiedPid", verifiedPid)
            .execute();
    }

    public void ensureUserWithAddressExist(String surname, LocalDate dateOfBirth, String postcode) {
        handle.createUpdate("insert into address (postcode) values ('" + postcode + "')").execute();
        int addressId = handle.createQuery("select address_id from address where postcode = :postcode")
            .bind("postcode", postcode)
            .mapTo(Integer.class)
            .findFirst()
            .get();
        handle.createUpdate("insert into person (surname, date_of_birth, address) values ('" + surname + "', '" + dateOfBirth + "', '" + addressId + "')").execute();
    }

    public boolean checkIfPidExist(String verifiedPid) {
        String pid = handle.select("select pid from verifiedPid where pid = ?", verifiedPid).mapTo(String.class).findOnly();
        return !pid.isEmpty();
    }

    public void eraseAllData() {
        handle.createUpdate("delete from verifiedPid").execute();
        handle.createUpdate("delete from person").execute();
        handle.createUpdate("delete from address").execute();
    }
}
