package uk.gov.ida.verifylocalmatchingserviceexample;

import io.dropwizard.jersey.setup.JerseyEnvironment;
import io.dropwizard.setup.Environment;
import org.junit.Before;
import org.junit.Test;
import uk.gov.ida.verifylocalmatchingserviceexample.configuration.DatabaseConfiguration;
import uk.gov.ida.verifylocalmatchingserviceexample.configuration.DatabaseMigrationSetup;
import uk.gov.ida.verifylocalmatchingserviceexample.configuration.VerifyLocalMatchingServiceExampleConfiguration;
import uk.gov.ida.verifylocalmatchingserviceexample.db.migration.DatabaseMigrationRunner;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ApplicationStartupTest {
    VerifyLocalMatchingServiceExampleConfiguration configuration;
    VerifyLocalMatchingServiceExampleApplication app;
    Environment mockEnvironment;
    DatabaseMigrationRunner mockDatabaseMigrationRunner;
    DatabaseMigrationSetup databaseMigrationSetup;

    @Before
    public void setup() {
        mockDatabaseMigrationRunner = mock(DatabaseMigrationRunner.class);

        VerifyLocalMatchingServiceExampleFactory mockFactory = mock(VerifyLocalMatchingServiceExampleFactory.class);
        when(mockFactory.getDatabaseMigrationRunner(any())).thenReturn(mockDatabaseMigrationRunner);

        app = new VerifyLocalMatchingServiceExampleApplication(mockFactory);

        DatabaseConfiguration mockDatabaseConfiguration = mock(DatabaseConfiguration.class);
        when(mockDatabaseConfiguration.getUrl()).thenReturn("http://example.com");

        mockEnvironment = mock(Environment.class);
        when(mockEnvironment.jersey()).thenReturn(mock(JerseyEnvironment.class));

        databaseMigrationSetup = mock(DatabaseMigrationSetup.class);

        configuration = mock(VerifyLocalMatchingServiceExampleConfiguration.class);
        when(configuration.getDatabaseConfiguration()).thenReturn(mockDatabaseConfiguration);
        when(configuration.getDatabaseMigrationSetup()).thenReturn(databaseMigrationSetup);
    }

    @Test
    public void shouldRunDatabaseMigrationsWhenConfiguredTo() throws Exception {
        when(databaseMigrationSetup.shouldRunDatabaseMigrations()).thenReturn(true);

        app.run(configuration, mockEnvironment);

        verify(mockDatabaseMigrationRunner).runDatabaseMigrations(any());
    }

    @Test
    public void shouldNotRunDatabaseMigrationsWhenNotConfiguredTo() throws Exception {
        when(databaseMigrationSetup.shouldRunDatabaseMigrations()).thenReturn(false);

        app.run(configuration, mockEnvironment);

        verify(mockDatabaseMigrationRunner, never()).runDatabaseMigrations(any());
    }
}
