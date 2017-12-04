package uk.gov.ida.verifylocalmatchingserviceexample.db.migration;

import io.dropwizard.db.DataSourceFactory;
import org.flywaydb.core.Flyway;

public class DatabaseMigrationRunner {
    private Flyway flyway;

    public DatabaseMigrationRunner(Flyway flyway) {
        this.flyway = flyway;
    }

    public void runDatabaseMigrations(DataSourceFactory dataSourceFactory) {
        Flyway flyway = new Flyway();
        flyway.setDataSource(dataSourceFactory.getUrl(), dataSourceFactory.getUser(), dataSourceFactory.getPassword());
        flyway.migrate();
    }
}
