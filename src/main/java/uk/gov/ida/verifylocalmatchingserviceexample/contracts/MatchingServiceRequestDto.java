package uk.gov.ida.verifylocalmatchingserviceexample.contracts;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MatchingServiceRequestDto {
    @Valid
    @NotNull
    @JsonProperty("matchingDataset")
    private MatchingAttributesDto matchingAttributesDto;
    @JsonProperty("cycle3Dataset")
    private Cycle3AttributesDto cycle3AttributesDto;
    @JsonProperty("hashedPid")
    @NotEmpty
    private String hashedPid;
    @JsonProperty("matchId")
    @NotEmpty
    private String matchId;
    @JsonProperty("levelOfAssurance")
    @NotNull
    private LevelOfAssuranceDto levelOfAssurance;

    public MatchingServiceRequestDto() {
    }

    public MatchingServiceRequestDto(MatchingAttributesDto matchingAttributesDto,
                                     Cycle3AttributesDto cycle3AttributesDto,
                                     String hashedPid,
                                     String matchId,
                                     LevelOfAssuranceDto levelOfAssurance) {
        this.matchingAttributesDto = matchingAttributesDto;
        this.cycle3AttributesDto = cycle3AttributesDto;
        this.hashedPid = hashedPid;
        this.matchId = matchId;
        this.levelOfAssurance = levelOfAssurance;
    }

    public MatchingAttributesDto getMatchingAttributesDto() {
        return matchingAttributesDto;
    }

    public Cycle3AttributesDto getCycle3AttributesDto() {
        return cycle3AttributesDto;
    }

    public String getHashedPid() {
        return hashedPid;
    }

    public String getMatchId() {
        return matchId;
    }

    public LevelOfAssuranceDto getLevelOfAssurance() {
        return levelOfAssurance;
    }
}
