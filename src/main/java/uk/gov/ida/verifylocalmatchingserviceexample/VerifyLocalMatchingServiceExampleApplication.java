package uk.gov.ida.verifylocalmatchingserviceexample;

import com.fasterxml.jackson.datatype.joda.JodaModule;
import io.dropwizard.Application;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.flywaydb.core.Flyway;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;
import uk.gov.ida.verifylocalmatchingserviceexample.configuration.VerifyLocalMatchingServiceExampleConfiguration;
import uk.gov.ida.verifylocalmatchingserviceexample.dao.VerifiedPid;
import uk.gov.ida.verifylocalmatchingserviceexample.resources.MatchingServiceResource;
import uk.gov.ida.verifylocalmatchingserviceexample.service.Cycle0MatchingService;

public class VerifyLocalMatchingServiceExampleApplication extends Application<VerifyLocalMatchingServiceExampleConfiguration>{
    private VerifyLocalMatchingServiceExampleFactory factory;

    public VerifyLocalMatchingServiceExampleApplication() {
        this(new VerifyLocalMatchingServiceExampleFactory());
    }

    public VerifyLocalMatchingServiceExampleApplication(VerifyLocalMatchingServiceExampleFactory factory) {
        this.factory = factory;
    }

    public static void main(String[] args) throws Exception {
        new VerifyLocalMatchingServiceExampleApplication().run(args);
    }

    @Override
    public void run(VerifyLocalMatchingServiceExampleConfiguration configuration, Environment environment) throws Exception {
        if (configuration.getShouldRunDatabaseMigrations()) {
            factory.getDatabaseMigrationRunner().runDatabaseMigrations(configuration.getDataSourceFactory());
        }

        Jdbi jdbi = Jdbi.create(configuration.getDataSourceFactory().getUrl());
        jdbi.installPlugin(new SqlObjectPlugin());
        VerifiedPid verifiedPid = jdbi.onDemand(VerifiedPid.class);
        Cycle0MatchingService cycle0MatchingService = new Cycle0MatchingService(verifiedPid);
        environment.jersey().register(new MatchingServiceResource(cycle0MatchingService));
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
