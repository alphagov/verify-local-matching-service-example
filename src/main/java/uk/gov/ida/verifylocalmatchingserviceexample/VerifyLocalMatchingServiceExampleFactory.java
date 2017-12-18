package uk.gov.ida.verifylocalmatchingserviceexample;

import com.codahale.metrics.health.HealthCheck;
import org.flywaydb.core.Flyway;
import org.jdbi.v3.core.Jdbi;
import uk.gov.ida.verifylocalmatchingserviceexample.dataaccess.PersonDAO;
import uk.gov.ida.verifylocalmatchingserviceexample.dataaccess.VerifiedPidDAO;
import uk.gov.ida.verifylocalmatchingserviceexample.db.migration.DatabaseMigrationRunner;
import uk.gov.ida.verifylocalmatchingserviceexample.healtchecks.DatabaseHealthCheck;
import uk.gov.ida.verifylocalmatchingserviceexample.resources.MatchingServiceResource;
import uk.gov.ida.verifylocalmatchingserviceexample.service.Cycle0MatchingService;
import uk.gov.ida.verifylocalmatchingserviceexample.service.Cycle1MatchingService;
import uk.gov.ida.verifylocalmatchingserviceexample.service.MatchingService;

public class VerifyLocalMatchingServiceExampleFactory {


    public DatabaseMigrationRunner getDatabaseMigrationRunner() {
        return new DatabaseMigrationRunner(new Flyway());
    }

    public MatchingServiceResource getMatchingService(Jdbi jdbi) {
        Cycle0MatchingService cycle0MatchingService = new Cycle0MatchingService(new VerifiedPidDAO(jdbi));
        Cycle1MatchingService cycle1MatchingService = new Cycle1MatchingService(new PersonDAO(jdbi));

        return new MatchingServiceResource(new MatchingService(cycle0MatchingService, cycle1MatchingService));
    }

    public HealthCheck getDatabaseHealthCheck(PersonDAO personDAO) {
        return new DatabaseHealthCheck(personDAO);
    }
}
