package uk.gov.ida.verifylocalmatchingserviceexample.dataaccess;

import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.statement.Update;

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

    public void save(String hashedPid, Integer personId) {
        jdbi.withHandle(handle -> handle.createUpdate("insert into verifiedPid (pid, person) values (:pid, :person)")
                .bind("pid", hashedPid)
                .bind("person", personId)
                .execute());
    }
}
