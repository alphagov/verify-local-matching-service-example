package uk.gov.ida.verifylocalmatchingserviceexample.contracts;

import com.fasterxml.jackson.annotation.JsonCreator;

import javax.validation.ValidationException;
import java.util.Optional;

public enum GenderDto {
    FEMALE("Female"),
    MALE("Male"),
    NOT_SPECIFIED("Not Specified");

    private final String value;

    GenderDto(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @JsonCreator
    public static GenderDto fromString(String value) {
        return Optional
                .ofNullable(GenderDto.valueOf(value))
                .orElseThrow(() -> new ValidationException(String.format("Unknown gender %s.", value)));
    }
}
