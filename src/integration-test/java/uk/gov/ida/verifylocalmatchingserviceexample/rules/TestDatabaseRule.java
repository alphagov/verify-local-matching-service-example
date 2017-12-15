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

    public Handle getHandle() {
        return handle;
    }

    @Override
    protected void before() throws Throwable {
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
        flyway.setLocations("classpath:db.migration.common",
                this.appRule.getConfiguration().getDatabaseMigrationSetup().getDatabaseEngine().getEngineSpecificMigrationsLocation());
        flyway.clean();
        flyway.migrate();
    }

    public void ensurePidExist(String verifiedPid) {
        handle.begin();
        handle.execute("insert into verifiedPid (pid, person) values('" + verifiedPid + "', (select person_id from person limit 1))");
        handle.commit();
    }

    public void ensurePidDoesNotExist(String verifiedPid) {
        handle.begin();
        handle.execute("delete from verifiedPid where pid =('" + verifiedPid + "')");
        handle.commit();
    }

    public void ensureUserExist(String surname, LocalDate dateOfBirth) {
        handle.begin();
        handle.execute("insert into person (surname, date_of_birth) values ('" + surname + "', '" + dateOfBirth + "')");
        handle.commit();
    }

    public void ensureUserDoesNotExist(String surname, LocalDate dateOfBirth) {
        handle.begin();
        handle.execute("delete from person where surname = ('" + surname + "') and date_of_birth = ('" + dateOfBirth + "')");
        handle.commit();
    }
}
