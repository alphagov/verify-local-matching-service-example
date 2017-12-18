package uk.gov.ida.verifylocalmatchingserviceexample.healtchecks;

import com.codahale.metrics.health.HealthCheck;
import uk.gov.ida.verifylocalmatchingserviceexample.dataaccess.PersonDAO;

import static com.codahale.metrics.health.HealthCheck.Result.healthy;
import static com.codahale.metrics.health.HealthCheck.Result.unhealthy;

public class DatabaseHealthCheck extends HealthCheck {

    private final PersonDAO personDAO;

    public DatabaseHealthCheck(PersonDAO personDAO) {
        this.personDAO = personDAO;
    }

    @Override
    protected Result check() {
        try {
            personDAO.tableExists();
            return healthy();
        } catch (Exception e) {
            return unhealthy("Error while connecting to the Database: " + e.getMessage());
        }
    }
}

