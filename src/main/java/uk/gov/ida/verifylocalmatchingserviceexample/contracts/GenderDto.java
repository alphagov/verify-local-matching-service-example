package uk.gov.ida.verifylocalmatchingserviceexample.contracts;

import com.fasterxml.jackson.annotation.JsonCreator;

import javax.validation.ValidationException;

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
        GenderDto gender = GenderDto.valueOf(value);
        if (gender == null)
            throw new ValidationException("Unknown gender: " + value);
        return gender;
    }
}
