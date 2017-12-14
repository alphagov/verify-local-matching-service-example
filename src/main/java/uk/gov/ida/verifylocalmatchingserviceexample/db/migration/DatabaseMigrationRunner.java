package uk.gov.ida.verifylocalmatchingserviceexample.db.migration;

import org.flywaydb.core.Flyway;
import uk.gov.ida.verifylocalmatchingserviceexample.configuration.DatabaseConfiguration;
import uk.gov.ida.verifylocalmatchingserviceexample.configuration.DatabaseEngine;

public class DatabaseMigrationRunner {
    private Flyway flyway;
    private DatabaseEngine databaseEngine;

    public DatabaseMigrationRunner(Flyway flyway, DatabaseEngine databaseEngine) {
        this.flyway = flyway;
        this.databaseEngine = databaseEngine;
    }

    public void runDatabaseMigrations(DatabaseConfiguration databaseConfiguration) {
        flyway.setDataSource(databaseConfiguration.getUrl(), databaseConfiguration.getUserName(), databaseConfiguration.getPassword());
        flyway.setBaselineVersionAsString("0");
        flyway.setBaselineOnMigrate(true);
        flyway.setLocations("classpath:db.migration.common", databaseEngine.getEngineSpecificMigrationsLocation());
        flyway.migrate();
    }
}
