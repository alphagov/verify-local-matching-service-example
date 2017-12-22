package uk.gov.ida.verifylocalmatchingserviceexample.builders;

import uk.gov.ida.verifylocalmatchingserviceexample.contracts.AddressDto;
import uk.gov.ida.verifylocalmatchingserviceexample.contracts.GenderDto;
import uk.gov.ida.verifylocalmatchingserviceexample.contracts.MatchingAttributesDto;
import uk.gov.ida.verifylocalmatchingserviceexample.contracts.MatchingAttributesValueDto;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static uk.gov.ida.verifylocalmatchingserviceexample.builders.AddressDtoBuilder.anAddressDtoBuilder;
import static uk.gov.ida.verifylocalmatchingserviceexample.builders.MatchingAttributesValueDtoBuilder.aMatchingAttributesValueDtoBuilder;

public class MatchingAttributesDtoBuilder {
    private MatchingAttributesValueDto<String> firstName = null;
    private MatchingAttributesValueDto<String> middleNames = null;
    private List<MatchingAttributesValueDto<String>> surnames = Arrays.asList(aMatchingAttributesValueDtoBuilder().withValue("default-surname").build());
    private MatchingAttributesValueDto<GenderDto> gender = null;
    private MatchingAttributesValueDto<LocalDate> dateOfBirth = aMatchingAttributesValueDtoBuilder().withValue(LocalDate.now()).build();
    private List<AddressDto> addresses = Arrays.asList(anAddressDtoBuilder().build());

    public static MatchingAttributesDtoBuilder aMatchingAttributesDtoBuilder() {
        return new MatchingAttributesDtoBuilder();
    }

    public MatchingAttributesDto build() {
        return new MatchingAttributesDto(firstName, middleNames, surnames, gender, dateOfBirth, addresses);
    }

    public MatchingAttributesDtoBuilder withFirstName(MatchingAttributesValueDto<String> firstName) {
        this.firstName = firstName;
        return this;
    }

    public MatchingAttributesDtoBuilder withMiddleNames(MatchingAttributesValueDto<String> middleNames) {
        this.middleNames = middleNames;
        return this;
    }

    public MatchingAttributesDtoBuilder withSurnames(List<MatchingAttributesValueDto<String>> surnames) {
        this.surnames = surnames;
        return this;
    }

    public MatchingAttributesDtoBuilder withSurname(MatchingAttributesValueDto<String> surname) {
        this.surnames = new LinkedList<MatchingAttributesValueDto<String>>() {{
            add(surname);
        }};
        return this;
    }

    public MatchingAttributesDtoBuilder withGender(MatchingAttributesValueDto<GenderDto> gender) {
        this.gender = gender;
        return this;
    }

    public MatchingAttributesDtoBuilder withDateOfBirth(MatchingAttributesValueDto<LocalDate> dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
        return this;
    }

    public MatchingAttributesDtoBuilder withAddresses(List<AddressDto> addresses) {
        this.addresses = addresses;
        return this;
    }
}
