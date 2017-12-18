package uk.gov.ida.verifylocalmatchingserviceexample;

import com.codahale.metrics.health.HealthCheckRegistry;
import io.dropwizard.jersey.setup.JerseyEnvironment;
import io.dropwizard.setup.Environment;
import org.junit.Before;
import org.junit.Test;
import uk.gov.ida.verifylocalmatchingserviceexample.configuration.DatabaseConfiguration;
import uk.gov.ida.verifylocalmatchingserviceexample.configuration.DatabaseMigrationSetup;
import uk.gov.ida.verifylocalmatchingserviceexample.configuration.VerifyLocalMatchingServiceExampleConfiguration;
import uk.gov.ida.verifylocalmatchingserviceexample.db.migration.DatabaseMigrationRunner;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ApplicationStartupTest {

    private VerifyLocalMatchingServiceExampleConfiguration configuration;
    private VerifyLocalMatchingServiceExampleApplication application;
    private Environment environment;
    private DatabaseMigrationRunner databaseMigrationRunner;
    private DatabaseMigrationSetup databaseMigrationSetup;

    @Before
    public void setup() {
        VerifyLocalMatchingServiceExampleFactory mockFactory = mock(VerifyLocalMatchingServiceExampleFactory.class);
        DatabaseConfiguration mockDatabaseConfiguration = mock(DatabaseConfiguration.class);

        application = new VerifyLocalMatchingServiceExampleApplication(mockFactory);
        databaseMigrationRunner = mock(DatabaseMigrationRunner.class);
        databaseMigrationSetup = mock(DatabaseMigrationSetup.class);
        environment = mock(Environment.class);
        configuration = mock(VerifyLocalMatchingServiceExampleConfiguration.class);


        when(mockFactory.getDatabaseMigrationRunner(any())).thenReturn(databaseMigrationRunner);
        when(mockDatabaseConfiguration.getUrl()).thenReturn("http://example.com");
        when(configuration.getDatabaseConfiguration()).thenReturn(mockDatabaseConfiguration);
        when(configuration.getDatabaseMigrationSetup()).thenReturn(databaseMigrationSetup);
        when(environment.jersey()).thenReturn(mock(JerseyEnvironment.class));
        when(environment.healthChecks()).thenReturn(mock(HealthCheckRegistry.class));
    }

    @Test
    public void shouldRunDatabaseMigrationsWhenConfiguredTo() throws Exception {
        when(databaseMigrationSetup.shouldRunDatabaseMigrations()).thenReturn(true);

        application.run(configuration, environment);

        verify(databaseMigrationRunner).runDatabaseMigrations(any());
    }

    @Test
    public void shouldNotRunDatabaseMigrationsWhenNotConfiguredTo() throws Exception {
        when(databaseMigrationSetup.shouldRunDatabaseMigrations()).thenReturn(false);

        application.run(configuration, environment);

        verify(databaseMigrationRunner, never()).runDatabaseMigrations(any());
    }
}
