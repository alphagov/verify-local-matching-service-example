package uk.gov.ida.verifylocalmatchingserviceexample.dataaccess;

import org.jdbi.v3.core.Jdbi;

import java.util.Optional;

public class VerifiedPidDAO {
    private Jdbi jdbi;

    public VerifiedPidDAO(Jdbi jdbi) {
        this.jdbi = jdbi;
    }

    public Optional<String> getVerifiedPid(String hashedPid) {
        return jdbi.withHandle(handle -> handle.select("select pid from verifiedPid where pid = ?", hashedPid)
                .mapTo(String.class)
                .findFirst()
        );
    }
}
