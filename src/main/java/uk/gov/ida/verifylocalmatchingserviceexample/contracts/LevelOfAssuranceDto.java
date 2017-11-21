package uk.gov.ida.verifylocalmatchingserviceexample.contracts;

import com.fasterxml.jackson.annotation.JsonCreator;

import javax.validation.ValidationException;
import java.util.Optional;

public enum LevelOfAssuranceDto {
    LEVEL_1,
    LEVEL_2,
    LEVEL_3,
    LEVEL_4;

    @JsonCreator
    public static LevelOfAssuranceDto fromString(String value) {
        return Optional
                .ofNullable(LevelOfAssuranceDto.valueOf(value))
                .orElseThrow(() -> new ValidationException(String.format("Unknown level of assurance %s.", value)));
    }
}
