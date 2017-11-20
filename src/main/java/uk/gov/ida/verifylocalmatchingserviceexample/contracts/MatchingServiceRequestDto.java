package uk.gov.ida.verifylocalmatchingserviceexample.contracts;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MatchingServiceRequestDto {
    @Valid
    @NotNull
    private MatchingAttributesDto matchingAttributesDto;
    private Cycle3AttributesDto cycle3AttributesDto;
    @JsonProperty("hashedPid")
    @NotEmpty
    private String hashedPid;
    @NotEmpty
    private String matchId;
    @NotNull
    private LevelOfAssuranceDto levelOfAssurance;

    @JsonCreator
    public MatchingServiceRequestDto(@JsonProperty("matchingDataset") MatchingAttributesDto matchingAttributesDto,
                                     @JsonProperty("cycle3Dataset") Cycle3AttributesDto cycle3AttributesDto,
                                     @JsonProperty("hashedPid") String hashedPid,
                                     @JsonProperty("matchId") String matchId,
                                     @JsonProperty("levelOfAssurance") LevelOfAssuranceDto levelOfAssurance) {
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
