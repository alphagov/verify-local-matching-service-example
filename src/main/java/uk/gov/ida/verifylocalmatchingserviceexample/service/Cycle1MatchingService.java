package uk.gov.ida.verifylocalmatchingserviceexample.service;

import uk.gov.ida.verifylocalmatchingserviceexample.contracts.MatchStatusResponseDto;
import uk.gov.ida.verifylocalmatchingserviceexample.contracts.MatchingAttributesValueDto;
import uk.gov.ida.verifylocalmatchingserviceexample.contracts.MatchingServiceRequestDto;
import uk.gov.ida.verifylocalmatchingserviceexample.dataaccess.PersonDAO;
import uk.gov.ida.verifylocalmatchingserviceexample.model.Person;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Cycle1MatchingService {

    private PersonDAO personDAO;

    public Cycle1MatchingService(PersonDAO personDAO) {
        this.personDAO = personDAO;
    }

    public MatchStatusResponseDto matchUser(MatchingServiceRequestDto matchingServiceRequest) {
        Optional<List<String>> surnames = Optional.ofNullable(getCurrentAndVerifiedSurname(matchingServiceRequest));
        Optional<List<Person>> matchingUsers = surnames
                .filter(s -> !s.isEmpty())
                .map(surnames1 -> personDAO.getMatchingUsers(surnames1));
        return matchingUsers
                .map(user -> user.size() == 1 ? MatchStatusResponseDto.MATCH : MatchStatusResponseDto.NO_MATCH)
                .orElse(MatchStatusResponseDto.NO_MATCH);
    }

    private List<String> getCurrentAndVerifiedSurname(MatchingServiceRequestDto matchingServiceRequest) {
        return matchingServiceRequest
                .getMatchingAttributesDto().getSurnames().stream()
                .filter(surname -> surname.getVerified() && surname.getTo() == null)
                .map(MatchingAttributesValueDto::getValue)
                .collect(Collectors.toList());
    }
}
