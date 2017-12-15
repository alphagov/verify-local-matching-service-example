package uk.gov.ida.verifylocalmatchingserviceexample;

import org.flywaydb.core.Flyway;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;
import uk.gov.ida.verifylocalmatchingserviceexample.configuration.DatabaseEngine;
import uk.gov.ida.verifylocalmatchingserviceexample.dataaccess.PersonDAO;
import uk.gov.ida.verifylocalmatchingserviceexample.dataaccess.VerifiedPidDAO;
import uk.gov.ida.verifylocalmatchingserviceexample.db.migration.DatabaseMigrationRunner;
import uk.gov.ida.verifylocalmatchingserviceexample.service.Cycle0MatchingService;
import uk.gov.ida.verifylocalmatchingserviceexample.service.Cycle1MatchingService;
import uk.gov.ida.verifylocalmatchingserviceexample.service.MatchingService;

public class VerifyLocalMatchingServiceExampleFactory {
    public DatabaseMigrationRunner getDatabaseMigrationRunner(DatabaseEngine databaseEngine) {
        return new DatabaseMigrationRunner(new Flyway(), databaseEngine);
    }

    public MatchingService getMatchingService(String url) {
        Jdbi jdbi = Jdbi.create(url);
        jdbi.installPlugin(new SqlObjectPlugin());

        Cycle0MatchingService cycle0MatchingService = new Cycle0MatchingService(new VerifiedPidDAO(jdbi));
        Cycle1MatchingService cycle1MatchingService = new Cycle1MatchingService(new PersonDAO(jdbi));
        return new MatchingService(cycle0MatchingService, cycle1MatchingService);
    }
}
