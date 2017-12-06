package uk.gov.ida.verifylocalmatchingserviceexample.configuration;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.Arrays;

public enum DatabaseEngine {
    POSTGRESQL("postgresql", "classpath:db.migration.postgresql"),
    SQLITE("sqlite", "classpath:db.migration.sqlite");

    private String engineName;
    private String engineSpecificMigrationsLocation;

    @JsonCreator
    public static DatabaseEngine fromString(String engineName) {
        if (engineName == null) return null;

        return Arrays.stream(values())
            .filter(x -> engineName.equals(x.engineName.toLowerCase()))
            .findFirst()
            .orElseThrow(() -> new RuntimeException(
                "Unsupported engine for database migrations: " + engineName + ". \n" +
                "Supported engines are: postgresql, sqlite"
            ));
    }

    DatabaseEngine(String engineName, String engineSpecificMigrationsLocation) {
        this.engineName = engineName;
        this.engineSpecificMigrationsLocation = engineSpecificMigrationsLocation;
    }

    public String getEngineSpecificMigrationsLocation() {
        return engineSpecificMigrationsLocation;
    }
}
