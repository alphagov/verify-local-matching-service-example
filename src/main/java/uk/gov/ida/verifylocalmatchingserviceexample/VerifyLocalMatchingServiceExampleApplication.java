package uk.gov.ida.verifylocalmatchingserviceexample;

import io.dropwizard.Application;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import uk.gov.ida.verifylocalmatchingserviceexample.resources.MatchingServiceResource;

public class VerifyLocalMatchingServiceExampleApplication extends Application<VerifyLocalMatchingServiceExampleConfiguration>{
    public static void main(String[] args) throws Exception {
        if(args.length == 0) {
            args[0] = "server";
            args[1] = "verify-local-matching-service-example.yml";
        }
        new VerifyLocalMatchingServiceExampleApplication().run(args);
    }

    @Override
    public void run(VerifyLocalMatchingServiceExampleConfiguration configuration, Environment environment) throws Exception {
        environment.jersey().register(new MatchingServiceResource());
    }

    @Override
    public void initialize(Bootstrap<VerifyLocalMatchingServiceExampleConfiguration> bootstrap) {
        // Enable variable substitution with environment variables
        bootstrap.setConfigurationSourceProvider(
                new SubstitutingSourceProvider(bootstrap.getConfigurationSourceProvider(),
                        new EnvironmentVariableSubstitutor(false)
                )
        );
    }
}
