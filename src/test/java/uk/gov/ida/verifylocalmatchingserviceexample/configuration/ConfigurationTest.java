package uk.gov.ida.verifylocalmatchingserviceexample.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidDefinitionException;
import io.dropwizard.jackson.Jackson;
import org.junit.Test;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.assertj.core.api.Java6Assertions.assertThatThrownBy;

public class ConfigurationTest {
    ObjectMapper objectMapper = Jackson.newObjectMapper();

    @Test
    public void shouldNotBeConfiguredToRunDatabaseMigrationsIfManagedDatabaseAttributeIsAbsent() throws Exception {
        String config = "{}";

        VerifyLocalMatchingServiceExampleConfiguration configuration = objectMapper.readValue(config, VerifyLocalMatchingServiceExampleConfiguration.class);

        assertThat(configuration.getDatabaseMigrationSetup().shouldRunDatabaseMigrations()).isFalse();
    }

    @Test
    public void shouldNotBeConfiguredToRunDatabaseMigrationsIfManagedDatabaseAttributeIsPresentButNoDatabaseEngineIsDefined() throws Exception {
        String config = "{\"managedDatabase\": {}}";

        VerifyLocalMatchingServiceExampleConfiguration configuration = objectMapper.readValue(config, VerifyLocalMatchingServiceExampleConfiguration.class);

        assertThat(configuration.getDatabaseMigrationSetup().shouldRunDatabaseMigrations()).isFalse();
    }

    @Test
    public void shouldBeConfiguredToRunDatabaseMigrationsIfManagedDatabaseAttributeIsPresentAndValidDatabaseEngineIsDefined() throws Exception {
        String config = "{\"managedDatabase\": {\"databaseEngine\": \"sqlite\"}}";

        VerifyLocalMatchingServiceExampleConfiguration configuration = objectMapper.readValue(config, VerifyLocalMatchingServiceExampleConfiguration.class);

        assertThat(configuration.getDatabaseMigrationSetup().shouldRunDatabaseMigrations()).isTrue();
    }

    @Test
    public void shouldErrorIfManagedDatabaseAttributeIsPresentAndUnsupportedForMigrationsDatabaseEngineIsDefined() throws Exception {
        String config = "{\"managedDatabase\": {\"databaseEngine\": \"unsupported\"}}";

        assertThatThrownBy(() -> objectMapper.readValue(config, VerifyLocalMatchingServiceExampleConfiguration.class))
            .isInstanceOf(InvalidDefinitionException.class)
            .hasMessageContaining("Unsupported engine for database migrations: unsupported. \n" +
                "Supported engines are: postgresql, sqlite");
    }
}
