package uk.gov.ida.verifylocalmatchingserviceexample.service;

import uk.gov.ida.verifylocalmatchingserviceexample.contracts.MatchStatusResponseDto;
import uk.gov.ida.verifylocalmatchingserviceexample.dataaccess.VerifiedPidDAO;

public class Cycle0MatchingService {
    private VerifiedPidDAO verifiedPid;

    public Cycle0MatchingService(VerifiedPidDAO verifiedPid) {
        this.verifiedPid = verifiedPid;
    }

    public MatchStatusResponseDto checkForPid(String hashedPid) {
        return verifiedPid.getVerifiedPid(hashedPid)
                .map(result -> MatchStatusResponseDto.MATCH)
                .orElse(MatchStatusResponseDto.NO_MATCH);
    }
}
