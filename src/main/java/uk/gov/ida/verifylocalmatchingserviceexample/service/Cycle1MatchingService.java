package uk.gov.ida.verifylocalmatchingserviceexample.service;

import uk.gov.ida.verifylocalmatchingserviceexample.contracts.MatchStatusResponseDto;
import uk.gov.ida.verifylocalmatchingserviceexample.contracts.MatchingAttributesValueDto;
import uk.gov.ida.verifylocalmatchingserviceexample.contracts.MatchingServiceRequestDto;
import uk.gov.ida.verifylocalmatchingserviceexample.dataaccess.PersonDAO;
import uk.gov.ida.verifylocalmatchingserviceexample.dataaccess.VerifiedPidDAO;
import uk.gov.ida.verifylocalmatchingserviceexample.model.Person;

import java.time.LocalDate;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static uk.gov.ida.verifylocalmatchingserviceexample.contracts.MatchStatusResponseDto.MATCH;
import static uk.gov.ida.verifylocalmatchingserviceexample.contracts.MatchStatusResponseDto.NO_MATCH;

public class Cycle1MatchingService {

    private PersonDAO personDAO;
    private VerifiedPidDAO verifiedPidDAO;

    public Cycle1MatchingService(PersonDAO personDAO, VerifiedPidDAO verifiedPidDAO) {
        this.personDAO = personDAO;
        this.verifiedPidDAO = verifiedPidDAO;
    }

    public MatchStatusResponseDto matchUser(MatchingServiceRequestDto matchingServiceRequest) {
        MatchingAttributesValueDto<LocalDate> dateOfBirth = matchingServiceRequest.getMatchingAttributesDto().getDateOfBirth();
        List<String> verifiedSurnames = getAllVerifiedSurnames(matchingServiceRequest.getMatchingAttributesDto().getSurnames());

        if (!dateOfBirth.getVerified() || verifiedSurnames.isEmpty()) {
            return NO_MATCH;
        }

        List<Person> matchingUsers = personDAO.getMatchingUsers(verifiedSurnames, dateOfBirth.getValue());

        if (matchingUsers.size() == 1){
            verifiedPidDAO.save(matchingServiceRequest.getHashedPid(), matchingUsers.get(0).getPersonId());
            return MATCH;
        }

        return NO_MATCH;
    }

    private List<String> getAllVerifiedSurnames(List<MatchingAttributesValueDto<String>> surnames) {
        return surnames.stream()
            .filter(MatchingAttributesValueDto::getVerified)
            .map(MatchingAttributesValueDto::getValue)
            .collect(toList());
    }
}
