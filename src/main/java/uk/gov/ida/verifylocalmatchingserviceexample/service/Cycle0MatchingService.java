package uk.gov.ida.verifylocalmatchingserviceexample.service;

import uk.gov.ida.verifylocalmatchingserviceexample.contracts.MatchStatusResponseDto;
import uk.gov.ida.verifylocalmatchingserviceexample.dao.VerifiedPid;

public class Cycle0MatchingService {
    private VerifiedPid verifiedPid;

    public Cycle0MatchingService(VerifiedPid verifiedPid) {
        this.verifiedPid = verifiedPid;
    }

    public MatchStatusResponseDto checkForPid(String hashedPid) {
        return verifiedPid.getVerifiedPID(hashedPid) == null ?
                MatchStatusResponseDto.NO_MATCH : MatchStatusResponseDto.MATCH;
    }
}
