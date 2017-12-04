package uk.gov.ida.verifylocalmatchingserviceexample;

import org.flywaydb.core.Flyway;
import uk.gov.ida.verifylocalmatchingserviceexample.db.migration.DatabaseMigrationRunner;

public class VerifyLocalMatchingServiceExampleFactory {
    public DatabaseMigrationRunner getDatabaseMigrationRunner() {
        return new DatabaseMigrationRunner(new Flyway());
    }
}
