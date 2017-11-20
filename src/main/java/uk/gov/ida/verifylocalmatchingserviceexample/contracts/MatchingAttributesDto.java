package uk.gov.ida.verifylocalmatchingserviceexample.contracts;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;
import org.joda.time.LocalDate;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MatchingAttributesDto {
    private MatchingAttributesValueDto<String> firstName;
    private MatchingAttributesValueDto<String> middleNames;
    @NotEmpty
    private List<MatchingAttributesValueDto<String>> surnames;
    private MatchingAttributesValueDto<GenderDto> gender;
    @NotNull
    private MatchingAttributesValueDto<LocalDate> dateOfBirth;
    @Valid
    @NotEmpty
    private List<AddressDto> addresses;

    @JsonCreator
    public MatchingAttributesDto(@JsonProperty("firstName") MatchingAttributesValueDto<String> firstName,
                                 @JsonProperty("middleNames") MatchingAttributesValueDto<String> middleNames,
                                 @JsonProperty("surnames") List<MatchingAttributesValueDto<String>> surnames,
                                 @JsonProperty("gender") MatchingAttributesValueDto<GenderDto> gender,
                                 @JsonProperty("dateOfBirth") MatchingAttributesValueDto<LocalDate> dateOfBirth,
                                 @JsonProperty("addresses") List<AddressDto> addresses) {
        this.firstName = firstName;
        this.middleNames = middleNames;
        this.surnames = surnames;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.addresses = addresses;
    }

    public MatchingAttributesValueDto<LocalDate> getDateOfBirth() {
        return dateOfBirth;
    }

    public MatchingAttributesValueDto<String> getFirstName() {
        return firstName;
    }

    public MatchingAttributesValueDto<String> getMiddleNames() {
        return middleNames;
    }

    public List<MatchingAttributesValueDto<String>> getSurnames() {
        return surnames;
    }

    public MatchingAttributesValueDto<GenderDto> getGender() {
        return gender;
    }

    public List<AddressDto> getAddresses() {
        return addresses;
    }
}
