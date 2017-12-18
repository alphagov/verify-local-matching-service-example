package uk.gov.ida.verifylocalmatchingserviceexample;

import com.codahale.metrics.health.HealthCheckRegistry;
import io.dropwizard.jersey.setup.JerseyEnvironment;
import io.dropwizard.setup.Environment;
import org.junit.Before;
import org.junit.Test;
import uk.gov.ida.verifylocalmatchingserviceexample.configuration.DatabaseConfiguration;
import uk.gov.ida.verifylocalmatchingserviceexample.configuration.VerifyLocalMatchingServiceExampleConfiguration;
import uk.gov.ida.verifylocalmatchingserviceexample.db.migration.DatabaseMigrationRunner;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ApplicationStartupTest {

    private VerifyLocalMatchingServiceExampleConfiguration configuration;
    private VerifyLocalMatchingServiceExampleApplication application;
    private Environment environment;
    private DatabaseMigrationRunner databaseMigrationRunner;

    @Before
    public void setup() {
        VerifyLocalMatchingServiceExampleFactory mockFactory = mock(VerifyLocalMatchingServiceExampleFactory.class);
        DatabaseConfiguration mockDatabaseConfiguration = mock(DatabaseConfiguration.class);

        application = new VerifyLocalMatchingServiceExampleApplication(mockFactory);
        databaseMigrationRunner = mock(DatabaseMigrationRunner.class);
        environment = mock(Environment.class);
        configuration = mock(VerifyLocalMatchingServiceExampleConfiguration.class);


        when(mockFactory.getDatabaseMigrationRunner()).thenReturn(databaseMigrationRunner);
        when(mockDatabaseConfiguration.getUrl()).thenReturn("http://example.com");
        when(configuration.getDatabaseConfiguration()).thenReturn(mockDatabaseConfiguration);
        when(environment.jersey()).thenReturn(mock(JerseyEnvironment.class));
        when(environment.healthChecks()).thenReturn(mock(HealthCheckRegistry.class));
    }

    @Test
    public void shouldAlwaysRunDatabaseMigrationsWhenApplicationStarts() throws Exception {
        application.run(configuration, environment);

        verify(databaseMigrationRunner).runDatabaseMigrations(any());
    }
}
