package uk.gov.ida.verifylocalmatchingserviceexample.configuration;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.db.DatabaseConfiguration;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@JsonIgnoreProperties(ignoreUnknown = true)
public class VerifyLocalMatchingServiceExampleConfiguration extends Configuration {
    @Valid
    @NotNull
    @JsonProperty("database")
    private DataSourceFactory database = new DataSourceFactory();

    @Valid
    @JsonProperty("useManagedDatabase")
    private boolean shouldRunDatabaseMigrations;

    public DataSourceFactory getDataSourceFactory() {
        return database;
    }

    public boolean getShouldRunDatabaseMigrations() {
        return shouldRunDatabaseMigrations;
    }
}
