package uk.gov.ida.verifylocalmatchingserviceexample.service;

import uk.gov.ida.verifylocalmatchingserviceexample.contracts.AddressDto;
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

    private Cycle3MatchingService cycle3MatchingService;
    private PersonDAO personDAO;
    private VerifiedPidDAO verifiedPidDAO;

    public Cycle1MatchingService(Cycle3MatchingService cycle3MatchingService, PersonDAO personDAO, VerifiedPidDAO verifiedPidDAO) {
        this.cycle3MatchingService = cycle3MatchingService;
        this.personDAO = personDAO;
        this.verifiedPidDAO = verifiedPidDAO;
    }

    public MatchStatusResponseDto matchUser(MatchingServiceRequestDto matchingServiceRequest) {
        MatchingAttributesValueDto<LocalDate> dateOfBirth = matchingServiceRequest.getMatchingAttributesDto().getDateOfBirth();
        List<String> verifiedSurnames = getVerifiedSurnames(matchingServiceRequest.getMatchingAttributesDto().getSurnames());

        if (!dateOfBirth.getVerified() || verifiedSurnames.isEmpty()) {
            return NO_MATCH;
        }

        List<Person> matchingUsers = personDAO.getMatchingUsers(verifiedSurnames, dateOfBirth.getValue());
        if (matchingUsers.size() < 1) {
            return NO_MATCH;
        }

        List<String> verifiedPostcodes = getVerifiedPostcodes(matchingServiceRequest.getMatchingAttributesDto().getAddresses());
        if (verifiedPostcodes.size() > 0) {
            matchingUsers = getMatchingUsersWithVerifiedPostCode(matchingUsers, verifiedPostcodes);
        }

        String verifiedFirstName = getVerifiedFirstName(matchingServiceRequest.getMatchingAttributesDto().getFirstName());
        if(verifiedFirstName == null) {
            return NO_MATCH;
        }

        matchingUsers = getMatchingUsersWithVerifiedFirstName(matchingUsers, verifiedFirstName);
        if (matchingUsers.size() == 1) {
            verifiedPidDAO.save(matchingServiceRequest.getHashedPid(), matchingUsers.get(0).getPersonId());
            return MATCH;
        }

        if(matchingUsers.size() > 1) {
            return cycle3MatchingService.matchUser(matchingServiceRequest, matchingUsers);
        }
        return NO_MATCH;
    }

    private List<Person> getMatchingUsersWithVerifiedFirstName(List<Person> matchingUsers, String firstName) {
        return matchingUsers.stream()
            .filter(person -> normalise(firstName).equals(normalise(person.getFirstName())))
            .collect(toList());
    }

    private List<Person> getMatchingUsersWithVerifiedPostCode(List<Person> matchingUsers, List<String> allVerifiedPostCodes) {
        return matchingUsers.stream()
            .filter(person -> isContainedIn(person.getAddress().getPostcode(), allVerifiedPostCodes))
            .collect(toList());
    }

    private boolean isContainedIn(String postcode, List<String> allVerifiedPostCodes) {
        return allVerifiedPostCodes.stream()
            .map(this::normalise)
            .anyMatch(item -> item.equals(normalise(postcode)));
    }

    private String normalise(String value) {
        return value.trim().toUpperCase().replace(" ", "");
    }

    private List<String> getVerifiedSurnames(List<MatchingAttributesValueDto<String>> surnames) {
        return surnames.stream()
            .filter(MatchingAttributesValueDto::getVerified)
            .map(MatchingAttributesValueDto::getValue)
            .collect(toList());
    }

    private List<String> getVerifiedPostcodes(List<AddressDto> addresses) {
        return addresses.stream()
            .filter(AddressDto::getVerified)
            .map(AddressDto::getPostCode)
            .filter(item -> !item.isEmpty())
            .collect(toList());
    }

    private String getVerifiedFirstName(MatchingAttributesValueDto<String> firstName) {
        return firstName != null && firstName.getVerified() ? firstName.getValue() : null;
    }
}
