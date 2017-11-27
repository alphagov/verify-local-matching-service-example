package uk.gov.ida.verifylocalmatchingserviceexample.contracts;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;
import org.joda.time.LocalDate;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MatchingAttributesDto {
    @JsonProperty("firstName")
    private MatchingAttributesValueDto<String> firstName;
    @JsonProperty("middleNames")
    private MatchingAttributesValueDto<String> middleNames;
    @JsonProperty("surnames")
    @NotEmpty
    private List<MatchingAttributesValueDto<String>> surnames;
    @JsonProperty("gender")
    private MatchingAttributesValueDto<GenderDto> gender;
    @JsonProperty("dateOfBirth")
    @Valid
    @NotNull
    private MatchingAttributesValueDto<LocalDate> dateOfBirth;
    @JsonProperty("addresses")
    @Valid
    @NotEmpty
    private List<AddressDto> addresses;

    public MatchingAttributesDto() {}

    public MatchingAttributesDto(MatchingAttributesValueDto<String> firstName,
                                 MatchingAttributesValueDto<String> middleNames,
                                 List<MatchingAttributesValueDto<String>> surnames,
                                 MatchingAttributesValueDto<GenderDto> gender,
                                 MatchingAttributesValueDto<LocalDate> dateOfBirth,
                                 List<AddressDto> addresses) {
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
