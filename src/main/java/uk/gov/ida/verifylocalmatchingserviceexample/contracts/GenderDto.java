package uk.gov.ida.verifylocalmatchingserviceexample.contracts;

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
}
