package uk.gov.ida.verifylocalmatchingserviceexample.db.migration;

import io.dropwizard.db.DataSourceFactory;
import org.flywaydb.core.Flyway;
import uk.gov.ida.verifylocalmatchingserviceexample.configuration.DatabaseEngine;

public class DatabaseMigrationRunner {
    private Flyway flyway;
    private DatabaseEngine databaseEngine;

    public DatabaseMigrationRunner(Flyway flyway, DatabaseEngine databaseEngine) {
        this.flyway = flyway;
        this.databaseEngine = databaseEngine;
    }

    public void runDatabaseMigrations(DataSourceFactory dataSourceFactory) {
        flyway.setDataSource(dataSourceFactory.getUrl(), dataSourceFactory.getUser(), dataSourceFactory.getPassword());
        flyway.setLocations("classpath:db.migration.common", databaseEngine.getEngineSpecificMigrationsLocation());
        flyway.migrate();
    }
}
