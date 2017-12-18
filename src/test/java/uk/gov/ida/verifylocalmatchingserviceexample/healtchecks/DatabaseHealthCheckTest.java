package uk.gov.ida.verifylocalmatchingserviceexample.healtchecks;

import org.junit.Before;
import org.junit.Test;
import uk.gov.ida.verifylocalmatchingserviceexample.dataaccess.PersonDAO;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DatabaseHealthCheckTest {

    private DatabaseHealthCheck healthCheck;

    private PersonDAO personDAO;

    @Before
    public void setUp(){
        personDAO = mock(PersonDAO.class);

        healthCheck = new DatabaseHealthCheck(personDAO);
    }

    @Test
    public void checkShouldReturnHealthyResultWhenDBAccessible() {
        when(personDAO.tableExists()).thenReturn(true);

        assertThat(healthCheck.check().isHealthy(), is(true));
    }

    @Test
    public void checkShouldReturnUnHealthyResultWhenDBNotAccessible() {
        when(personDAO.tableExists()).thenThrow(new RuntimeException("any-exception"));

        assertThat(healthCheck.check().isHealthy(), is(false));
    }
}