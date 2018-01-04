package uk.gov.ida.verifylocalmatchingserviceexample.service;

import uk.gov.ida.verifylocalmatchingserviceexample.dataaccess.VerifiedPidDAO;

import java.util.Optional;

public class Cycle0MatchingService {
    private VerifiedPidDAO verifiedPid;

    public Cycle0MatchingService(VerifiedPidDAO verifiedPid) {
        this.verifiedPid = verifiedPid;
    }

    public Optional<String> checkForPid(String hashedPid) {
        return verifiedPid.getVerifiedPid(hashedPid);
    }
}
