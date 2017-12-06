package uk.gov.ida.verifylocalmatchingserviceexample.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DatabaseMigrationSetup {
    private DatabaseEngine databaseEngine;

    public DatabaseMigrationSetup (
        @JsonProperty("databaseEngine") DatabaseEngine databaseEngine
    ) {
        this.databaseEngine = databaseEngine;
    }

    public boolean shouldRunDatabaseMigrations() {
        return databaseEngine != null;
    }

    public DatabaseEngine getDatabaseEngine() {
        return databaseEngine;
    }
}
