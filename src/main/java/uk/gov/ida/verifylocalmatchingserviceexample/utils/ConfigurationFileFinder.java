package uk.gov.ida.verifylocalmatchingserviceexample.utils;

import uk.gov.ida.verifylocalmatchingserviceexample.VerifyLocalMatchingServiceExampleApplication;

import java.io.File;

public class ConfigurationFileFinder {
    public static String getConfigurationFilePath() {
        String applicationClassPath =
            VerifyLocalMatchingServiceExampleApplication.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        String configFilePath = applicationClassPath
            .replaceFirst("lib/verify-local-matching-service-example.jar$", "verify-local-matching-service-example.yml")
            .replaceFirst("build/classes/main/$", "verify-local-matching-service-example.yml");

        if (new File(configFilePath).exists()) {
            return configFilePath;
        }

        throw new RuntimeException(
            String.format(
                "Unable to locate configuration file at %s. " +
                "Please ensure a config file is present here, or provide location as a command line argument",
                configFilePath)
        );
    }
}
