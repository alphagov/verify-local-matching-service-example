package uk.gov.ida.verifylocalmatchingserviceexample.builders;

import uk.gov.ida.verifylocalmatchingserviceexample.contracts.Cycle3AttributesDto;
import uk.gov.ida.verifylocalmatchingserviceexample.contracts.LevelOfAssuranceDto;
import uk.gov.ida.verifylocalmatchingserviceexample.contracts.MatchingAttributesDto;
import uk.gov.ida.verifylocalmatchingserviceexample.contracts.MatchingServiceRequestDto;

import static uk.gov.ida.verifylocalmatchingserviceexample.builders.MatchingAttributesDtoBuilder.aMatchingAttributesDtoBuilder;

public class MatchingServiceRequestDtoBuilder {
    private MatchingAttributesDto matchingAttributesDto = aMatchingAttributesDtoBuilder().build();
    private Cycle3AttributesDto cycle3AttributesDto = null;
    private String hashedPid = "default-hashed-pid";
    private String matchId = "default-match-id";
    private LevelOfAssuranceDto levelOfAssurance = LevelOfAssuranceDto.LEVEL_1;

    public static MatchingServiceRequestDtoBuilder aMatchingServiceRequestDtoBuilder() {
        return new MatchingServiceRequestDtoBuilder();
    }

    public MatchingServiceRequestDto build() {
        return new MatchingServiceRequestDto(matchingAttributesDto, cycle3AttributesDto, hashedPid, matchId, levelOfAssurance);
    }

    public MatchingServiceRequestDtoBuilder withMatchingAttributesDto(MatchingAttributesDto matchingAttributesDto) {
        this.matchingAttributesDto = matchingAttributesDto;
        return this;
    }

    public MatchingServiceRequestDtoBuilder withCycle3AttributesDto(Cycle3AttributesDto cycle3AttributesDto) {
        this.cycle3AttributesDto = cycle3AttributesDto;
        return this;
    }

    public MatchingServiceRequestDtoBuilder withHashedPid(String hashedPid) {
        this.hashedPid = hashedPid;
        return this;
    }

    public MatchingServiceRequestDtoBuilder withMatchId(String matchId) {
        this.matchId = matchId;
        return this;
    }

    public MatchingServiceRequestDtoBuilder withLevelOfAssuranceDto(LevelOfAssuranceDto levelOfAssurance) {
        this.levelOfAssurance = levelOfAssurance;
        return this;
    }
}
