package uk.gov.ida.verifylocalmatchingserviceexample.service;

import uk.gov.ida.verifylocalmatchingserviceexample.contracts.Cycle3AttributesDto;
import uk.gov.ida.verifylocalmatchingserviceexample.contracts.MatchStatusResponseDto;
import uk.gov.ida.verifylocalmatchingserviceexample.contracts.MatchingServiceRequestDto;
import uk.gov.ida.verifylocalmatchingserviceexample.dataaccess.NationalInsuranceNumberDAO;
import uk.gov.ida.verifylocalmatchingserviceexample.dataaccess.VerifiedPidDAO;
import uk.gov.ida.verifylocalmatchingserviceexample.model.Person;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static uk.gov.ida.verifylocalmatchingserviceexample.contracts.MatchStatusResponseDto.MATCH;
import static uk.gov.ida.verifylocalmatchingserviceexample.contracts.MatchStatusResponseDto.NO_MATCH;

public class Cycle3MatchingService {

    private static final String NATIONAL_INSURANCE_NUMBER = "nationalInsuranceNumber";
    private NationalInsuranceNumberDAO nationalInsuranceNumberDAO;
    private VerifiedPidDAO verifiedPidDAO;

    public Cycle3MatchingService(NationalInsuranceNumberDAO nationalInsuranceNumberDAO, VerifiedPidDAO verifiedPidDAO) {
        this.nationalInsuranceNumberDAO = nationalInsuranceNumberDAO;
        this.verifiedPidDAO = verifiedPidDAO;
    }

    public MatchStatusResponseDto matchUser(MatchingServiceRequestDto matchingServiceRequest, List<Person> cycle1MatchingUsers) {
        Cycle3AttributesDto cycle3AttributesDto = matchingServiceRequest.getCycle3AttributesDto();
        if (!isNationalInsuranceNumberPresent(cycle3AttributesDto)) {
            return NO_MATCH;
        }

        List<Integer> personIds = cycle1MatchingUsers.stream().map(Person::getPersonId).collect(toList());
        List<Integer> matchingUserIds = nationalInsuranceNumberDAO
            .getMatchingUserIds(personIds, cycle3AttributesDto.getAttributes().get(NATIONAL_INSURANCE_NUMBER));

        if (matchingUserIds.size() == 1) {
            verifiedPidDAO.save(matchingServiceRequest.getHashedPid(), matchingUserIds.get(0));
            return MATCH;
        }
        return NO_MATCH;
    }

    private boolean isNationalInsuranceNumberPresent(Cycle3AttributesDto cycle3AttributesDto) {
        return isCycle3AttributePresent(cycle3AttributesDto) &&
            cycle3AttributesDto.getAttributes().containsKey(NATIONAL_INSURANCE_NUMBER) &&
            cycle3AttributesDto.getAttributes().get(NATIONAL_INSURANCE_NUMBER) != null;
    }

    private boolean isCycle3AttributePresent(Cycle3AttributesDto cycle3AttributesDto) {
        return cycle3AttributesDto != null && !cycle3AttributesDto.getAttributes().isEmpty();
    }
}
