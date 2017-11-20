package uk.gov.ida.verifylocalmatchingserviceexample.contracts;

import com.fasterxml.jackson.annotation.JsonCreator;

import javax.validation.ValidationException;

public enum LevelOfAssuranceDto {
    LEVEL_1,
    LEVEL_2,
    LEVEL_3,
    LEVEL_4;

    @JsonCreator
    public static LevelOfAssuranceDto fromString(String value) {
        LevelOfAssuranceDto levelOfAssurance = LevelOfAssuranceDto.valueOf(value);
        if (levelOfAssurance == null)
            throw new ValidationException("Unknown level of assurance: " + value);
        return levelOfAssurance;
    }
}
