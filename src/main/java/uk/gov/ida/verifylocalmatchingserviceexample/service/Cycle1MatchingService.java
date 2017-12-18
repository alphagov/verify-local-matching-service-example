package uk.gov.ida.verifylocalmatchingserviceexample.service;

import uk.gov.ida.verifylocalmatchingserviceexample.contracts.MatchStatusResponseDto;
import uk.gov.ida.verifylocalmatchingserviceexample.contracts.MatchingAttributesValueDto;
import uk.gov.ida.verifylocalmatchingserviceexample.contracts.MatchingServiceRequestDto;
import uk.gov.ida.verifylocalmatchingserviceexample.dataaccess.PersonDAO;
import uk.gov.ida.verifylocalmatchingserviceexample.model.Person;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Cycle1MatchingService {

    private PersonDAO personDAO;

    public Cycle1MatchingService(PersonDAO personDAO) {
        this.personDAO = personDAO;
    }

    public MatchStatusResponseDto matchUser(MatchingServiceRequestDto matchingServiceRequest) {
        Optional<List<String>> surnames = getCurrentAndVerifiedSurnames(matchingServiceRequest.getMatchingAttributesDto().getSurnames());
        Optional<LocalDate> dateOfBirth = getVerifiedDateOfBirth(matchingServiceRequest);
        Optional<List<Person>> matchingUsers = surnames
                .filter(s -> !s.isEmpty())
                .flatMap(surname -> dateOfBirth.map(d -> personDAO.getMatchingUsers(surname, d)));

        return matchingUsers.filter(user -> user.size() == 1).map(user -> MatchStatusResponseDto.MATCH).orElse(MatchStatusResponseDto.NO_MATCH);
    }

    private Optional<LocalDate> getVerifiedDateOfBirth(MatchingServiceRequestDto matchingServiceRequest) {
        MatchingAttributesValueDto<LocalDate> dateOfBirth = matchingServiceRequest.getMatchingAttributesDto().getDateOfBirth();
        return Stream.of(dateOfBirth)
                .filter(MatchingAttributesValueDto::getVerified)
                .map(MatchingAttributesValueDto::getValue)
                .findFirst();
    }

    private Optional<List<String>> getCurrentAndVerifiedSurnames(List<MatchingAttributesValueDto<String>> surnames) {
        return Optional.ofNullable(surnames.stream()
                .filter(surname -> surname.getVerified() && surname.getTo() == null)
                .map(MatchingAttributesValueDto::getValue)
                .collect(Collectors.toList()));
    }
}
