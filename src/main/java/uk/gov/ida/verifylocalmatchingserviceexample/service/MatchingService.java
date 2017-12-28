package uk.gov.ida.verifylocalmatchingserviceexample.service;

import uk.gov.ida.verifylocalmatchingserviceexample.contracts.MatchStatusResponseDto;
import uk.gov.ida.verifylocalmatchingserviceexample.contracts.MatchingServiceRequestDto;

import static uk.gov.ida.verifylocalmatchingserviceexample.contracts.MatchStatusResponseDto.MATCH;

public class MatchingService {

    private Cycle0MatchingService cycle0MatchingService;
    private Cycle1MatchingService cycle1MatchingService;

    public MatchingService(Cycle0MatchingService cycle0MatchingService, Cycle1MatchingService cycle1MatchingService) {
        this.cycle0MatchingService = cycle0MatchingService;
        this.cycle1MatchingService = cycle1MatchingService;
    }

    public MatchStatusResponseDto findMatchingUser(MatchingServiceRequestDto matchingServiceRequest) {
        MatchStatusResponseDto cycle0MatchStatus = cycle0MatchingService
            .checkForPid(matchingServiceRequest.getHashedPid());

        return cycle0MatchStatus == MATCH ? cycle0MatchStatus : cycle1MatchingService.matchUser(matchingServiceRequest);
    }
}
