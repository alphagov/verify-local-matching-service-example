package uk.gov.ida.verifylocalmatchingserviceexample;

import com.fasterxml.jackson.datatype.joda.JodaModule;
import io.dropwizard.Application;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.jdbi.v3.core.Jdbi;
import uk.gov.ida.verifylocalmatchingserviceexample.configuration.VerifyLocalMatchingServiceExampleConfiguration;
import uk.gov.ida.verifylocalmatchingserviceexample.dataaccess.PersonDAO;
import uk.gov.ida.verifylocalmatchingserviceexample.resources.MatchingServiceResource;
import uk.gov.ida.verifylocalmatchingserviceexample.utils.ConfigurationFileFinder;

import java.util.Arrays;

public class VerifyLocalMatchingServiceExampleApplication extends Application<VerifyLocalMatchingServiceExampleConfiguration> {
    private VerifyLocalMatchingServiceExampleFactory factory;

    public VerifyLocalMatchingServiceExampleApplication() {
        this(new VerifyLocalMatchingServiceExampleFactory());
    }

    public VerifyLocalMatchingServiceExampleApplication(VerifyLocalMatchingServiceExampleFactory factory) {
        this.factory = factory;
    }

    public static void main(String[] args) throws Exception {
        if (Arrays.asList(args).isEmpty()) {
            String configFilePath = ConfigurationFileFinder.getConfigurationFilePath();
            new VerifyLocalMatchingServiceExampleApplication().run("server", configFilePath);
        } else {
            new VerifyLocalMatchingServiceExampleApplication().run(args);
        }
    }

    @Override
    public void run(VerifyLocalMatchingServiceExampleConfiguration configuration, Environment environment) throws Exception {
        factory.getDatabaseMigrationRunner().runDatabaseMigrations(configuration.getDatabaseConfiguration());

        Jdbi jdbi = Jdbi.create(configuration.getDatabaseConfiguration().getUrl());

        MatchingServiceResource matchingServiceResource = factory.getMatchingService(jdbi);
        environment.jersey().register(matchingServiceResource);
        environment.healthChecks().register("database", factory.getDatabaseHealthCheck(new PersonDAO(jdbi)));
    }

    @Override
    public void initialize(Bootstrap<VerifyLocalMatchingServiceExampleConfiguration> bootstrap) {
        // Enable variable substitution with environment variables
        bootstrap.setConfigurationSourceProvider(
                new SubstitutingSourceProvider(bootstrap.getConfigurationSourceProvider(),
                        new EnvironmentVariableSubstitutor(false)
                )
        );
        bootstrap.getObjectMapper().registerModule(new JodaModule());
    }
}
