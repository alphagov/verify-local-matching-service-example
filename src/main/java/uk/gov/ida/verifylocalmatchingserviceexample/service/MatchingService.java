package uk.gov.ida.verifylocalmatchingserviceexample.service;

import uk.gov.ida.verifylocalmatchingserviceexample.contracts.MatchStatusResponseDto;
import uk.gov.ida.verifylocalmatchingserviceexample.contracts.MatchingServiceRequestDto;
import uk.gov.ida.verifylocalmatchingserviceexample.model.Person;

import java.util.List;
import java.util.Optional;

import static uk.gov.ida.verifylocalmatchingserviceexample.contracts.MatchStatusResponseDto.MATCH;
import static uk.gov.ida.verifylocalmatchingserviceexample.contracts.MatchStatusResponseDto.NO_MATCH;

public class MatchingService {

    private Cycle0MatchingService cycle0MatchingService;
    private Cycle1MatchingService cycle1MatchingService;
    private Cycle3MatchingService cycle3MatchingService;

    public MatchingService(Cycle0MatchingService cycle0MatchingService,
                           Cycle1MatchingService cycle1MatchingService,
                           Cycle3MatchingService cycle3MatchingService) {
        this.cycle0MatchingService = cycle0MatchingService;
        this.cycle1MatchingService = cycle1MatchingService;
        this.cycle3MatchingService = cycle3MatchingService;
    }

    public MatchStatusResponseDto findMatchingUser(MatchingServiceRequestDto matchingServiceRequest) {
        Optional<String> cycle0MatchingUserPid = cycle0MatchingService.checkForPid(matchingServiceRequest.getHashedPid());
        if (cycle0MatchingUserPid.isPresent()) {
            return MATCH;
        }

        List<Person> cycle1MatchingUsers = cycle1MatchingService.matchUser(matchingServiceRequest);
        if (checkForSingleUserMatch(cycle1MatchingUsers)) {
            return MATCH;
        }

        if (checkForMultipleMatchingUsers(cycle1MatchingUsers)) {
            List<Integer> cycle3MatchingUserIds = cycle3MatchingService.matchUser(matchingServiceRequest, cycle1MatchingUsers);
            if (checkForSingleUserMatch(cycle3MatchingUserIds)) {
                return MATCH;
            }
        }

        return NO_MATCH;
    }

    private boolean checkForMultipleMatchingUsers(List<Person> cycle1MatchingUsers) {
        return cycle1MatchingUsers != null && cycle1MatchingUsers.size() > 1;
    }

    private boolean checkForSingleUserMatch(List cycle1MatchingUsers) {
        return cycle1MatchingUsers != null && cycle1MatchingUsers.size() == 1;
    }
}
