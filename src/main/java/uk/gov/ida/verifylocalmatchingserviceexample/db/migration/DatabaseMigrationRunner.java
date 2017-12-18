package uk.gov.ida.verifylocalmatchingserviceexample.db.migration;

import org.flywaydb.core.Flyway;
import uk.gov.ida.verifylocalmatchingserviceexample.configuration.DatabaseConfiguration;

public class DatabaseMigrationRunner {
    private Flyway flyway;

    public DatabaseMigrationRunner(Flyway flyway) {
        this.flyway = flyway;
    }

    public void runDatabaseMigrations(DatabaseConfiguration databaseConfiguration) {
        flyway.setDataSource(databaseConfiguration.getUrl(), databaseConfiguration.getUserName(), databaseConfiguration.getPassword());
        flyway.setBaselineVersionAsString("0");
        flyway.setBaselineOnMigrate(true);
        flyway.setLocations("classpath:db.migration");
        flyway.migrate();
    }
}
