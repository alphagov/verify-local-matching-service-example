package uk.gov.ida.verifylocalmatchingserviceexample.service;

import uk.gov.ida.verifylocalmatchingserviceexample.contracts.MatchStatusResponseDto;
import uk.gov.ida.verifylocalmatchingserviceexample.dao.VerifiedPidDAO;

public class Cycle0MatchingService {
    private VerifiedPidDAO verifiedPidDAO;

    public Cycle0MatchingService(VerifiedPidDAO verifiedPidDAO) {
        this.verifiedPidDAO = verifiedPidDAO;
    }

    public MatchStatusResponseDto checkForPid(String hashedPid) {
        return verifiedPidDAO.getVerifiedPID(hashedPid) == null ?
                MatchStatusResponseDto.NO_MATCH : MatchStatusResponseDto.MATCH;
    }
}
