package uk.gov.ida.verifylocalmatchingserviceexample;

import org.flywaydb.core.Flyway;
import uk.gov.ida.verifylocalmatchingserviceexample.configuration.DatabaseEngine;
import uk.gov.ida.verifylocalmatchingserviceexample.configuration.VerifyLocalMatchingServiceExampleConfiguration;
import uk.gov.ida.verifylocalmatchingserviceexample.db.migration.DatabaseMigrationRunner;

public class VerifyLocalMatchingServiceExampleFactory {
    public DatabaseMigrationRunner getDatabaseMigrationRunner(DatabaseEngine databaseEngine) {
        return new DatabaseMigrationRunner(new Flyway(), databaseEngine);
    }
}
