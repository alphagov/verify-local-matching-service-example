package uk.gov.ida.verifylocalmatchingserviceexample.service;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Answers;
import uk.gov.ida.verifylocalmatchingserviceexample.contracts.AddressDto;
import uk.gov.ida.verifylocalmatchingserviceexample.contracts.MatchStatusResponseDto;
import uk.gov.ida.verifylocalmatchingserviceexample.contracts.MatchingAttributesValueDto;
import uk.gov.ida.verifylocalmatchingserviceexample.contracts.MatchingServiceRequestDto;
import uk.gov.ida.verifylocalmatchingserviceexample.dataaccess.PersonDAO;
import uk.gov.ida.verifylocalmatchingserviceexample.dataaccess.VerifiedPidDAO;
import uk.gov.ida.verifylocalmatchingserviceexample.model.Person;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.ida.verifylocalmatchingserviceexample.builders.AddressBuilder.anAddress;
import static uk.gov.ida.verifylocalmatchingserviceexample.builders.AddressDtoBuilder.anAddressDtoBuilder;
import static uk.gov.ida.verifylocalmatchingserviceexample.builders.MatchingAttributesDtoBuilder.aMatchingAttributesDtoBuilder;
import static uk.gov.ida.verifylocalmatchingserviceexample.builders.MatchingAttributesValueDtoBuilder.aMatchingAttributesValueDtoBuilder;
import static uk.gov.ida.verifylocalmatchingserviceexample.builders.MatchingServiceRequestDtoBuilder.aMatchingServiceRequestDtoBuilder;
import static uk.gov.ida.verifylocalmatchingserviceexample.builders.PersonBuilder.aPerson;
import static uk.gov.ida.verifylocalmatchingserviceexample.builders.PersonBuilder.anyPerson;
import static uk.gov.ida.verifylocalmatchingserviceexample.contracts.MatchStatusResponseDto.MATCH;
import static uk.gov.ida.verifylocalmatchingserviceexample.contracts.MatchStatusResponseDto.NO_MATCH;

public class Cycle1MatchingServiceTest {

    private PersonDAO personDAO;
    private VerifiedPidDAO verifiedPidDAO;
    private Cycle1MatchingService cycle1MatchingService;
    private MatchingServiceRequestDto matchingServiceRequestDto;
    private MatchingAttributesValueDto<LocalDate> anyValidatedDateOfBirth;
    private List anyListOfSurnamesWithValidatedValue;

    @Before
    public void setUp() {
        personDAO = mock(PersonDAO.class);
        verifiedPidDAO = mock(VerifiedPidDAO.class);
        matchingServiceRequestDto = mock(MatchingServiceRequestDto.class, Answers.RETURNS_DEEP_STUBS);

        cycle1MatchingService = new Cycle1MatchingService(personDAO, verifiedPidDAO);

        anyValidatedDateOfBirth = aMatchingAttributesValueDtoBuilder().withValue(LocalDate.now()).withVerified(true).build();
        anyListOfSurnamesWithValidatedValue = Arrays.asList(
            aMatchingAttributesValueDtoBuilder().withValue("any-surname-one").withVerified(true).build(),
            aMatchingAttributesValueDtoBuilder().withValue("any-surname-two").withVerified(true).build(),
            aMatchingAttributesValueDtoBuilder().withValue("any-surname-three").withVerified(false).build()
        );
    }

    @Test
    public void shouldReturnNoMatchWhenDateOfBirthIsNotVerified() {
        MatchingAttributesValueDto<LocalDate> dateOfBirth = matchingServiceRequestDto.getMatchingAttributesDto().getDateOfBirth();

        when(dateOfBirth.getVerified()).thenReturn(false);
        when(matchingServiceRequestDto.getMatchingAttributesDto().getSurnames()).thenReturn(anyListOfSurnamesWithValidatedValue);

        assertThat(cycle1MatchingService.matchUser(matchingServiceRequestDto)).isEqualTo(NO_MATCH);
        verify(personDAO, never()).getMatchingUsers(any(), any());
    }

    @Test
    public void shouldReturnNoMatchWhenThereAreNoVerifiedSurnames() {
        List unverifiedSurnameAttributeValues = Arrays.asList(
            aMatchingAttributesValueDtoBuilder().withValue("some-surname-one").withVerified(false).build()
        );

        when(matchingServiceRequestDto.getMatchingAttributesDto().getSurnames()).thenReturn(unverifiedSurnameAttributeValues);
        when(matchingServiceRequestDto.getMatchingAttributesDto().getDateOfBirth()).thenReturn(anyValidatedDateOfBirth);

        assertThat(cycle1MatchingService.matchUser(matchingServiceRequestDto)).isEqualTo(NO_MATCH);
    }

    @Test
    public void shouldReturnNoMatchWhenThereAreNoUsersWithGivenSurnamesAndDateOfBirth() {
        when(matchingServiceRequestDto.getMatchingAttributesDto().getSurnames()).thenReturn(anyListOfSurnamesWithValidatedValue);
        when(matchingServiceRequestDto.getMatchingAttributesDto().getDateOfBirth()).thenReturn(anyValidatedDateOfBirth);
        when(personDAO.getMatchingUsers(anyListOfSurnamesWithValidatedValue, anyValidatedDateOfBirth.getValue())).thenReturn(emptyList());

        assertThat(cycle1MatchingService.matchUser(matchingServiceRequestDto)).isEqualTo(NO_MATCH);
    }

    @Test
    public void shouldReturnNoMatchWhenThereIsNoVerifiedPostCode() {
        List<AddressDto> unverifiedPostcodeAttributeValues = Arrays.asList(
            anAddressDtoBuilder().withVerified(false).build()
        );

        when(matchingServiceRequestDto.getMatchingAttributesDto().getSurnames()).thenReturn(anyListOfSurnamesWithValidatedValue);
        when(matchingServiceRequestDto.getMatchingAttributesDto().getDateOfBirth()).thenReturn(anyValidatedDateOfBirth);
        when(personDAO.getMatchingUsers(any(), any())).thenReturn(Arrays.asList(anyPerson()));
        when(matchingServiceRequestDto.getMatchingAttributesDto().getAddresses()).thenReturn(unverifiedPostcodeAttributeValues);

        assertThat(cycle1MatchingService.matchUser(matchingServiceRequestDto)).isEqualTo(NO_MATCH);

        verify(matchingServiceRequestDto.getMatchingAttributesDto(), times(1)).getAddresses();
    }

    @Test
    public void shouldReturnNoMatchWhenRequestHasVerifiedSurnameButIsNotCurrentSurname() {
        LocalDate dateOfBirth = LocalDate.of(1993, 12, 16);
        MatchingAttributesValueDto verifiedDateOfBirth = aMatchingAttributesValueDtoBuilder()
            .withVerified(true)
            .withValue(dateOfBirth).build();
        cycle1MatchingService = new Cycle1MatchingService(personDAO, verifiedPidDAO);

        MatchingServiceRequestDto requestWithVerifiedButNotCurrentSurname = aMatchingServiceRequestDtoBuilder()
            .withMatchingAttributesDto(aMatchingAttributesDtoBuilder()
                .withSurname(aMatchingAttributesValueDtoBuilder().withTo(DateTime.now()).withValue("test-surname").build())
                .withDateOfBirth(verifiedDateOfBirth)
                .build())
            .build();
        MatchStatusResponseDto matchStatusResponseDto = cycle1MatchingService.matchUser(
            requestWithVerifiedButNotCurrentSurname);

        verify(personDAO, never()).getMatchingUsers(anyList(), any());
        assertThat(matchStatusResponseDto).isEqualTo(NO_MATCH);
    }

    @Test
    public void shouldReturnNoMatchWhenNoHistoricalPostCodeCanBeMatched() {
        Person person = aPerson().withAddress(anAddress().withPostcode("some-postcode").build()).build();
        List<AddressDto> addresses = Arrays.asList(
            anAddressDtoBuilder().withPostCode("non-matching-postcode-one").withVerified(true).build(),
            anAddressDtoBuilder().withPostCode("non-matching-postcode-two").withVerified(true).build()
        );

        when(matchingServiceRequestDto.getMatchingAttributesDto().getSurnames()).thenReturn(anyListOfSurnamesWithValidatedValue);
        when(matchingServiceRequestDto.getMatchingAttributesDto().getDateOfBirth()).thenReturn(anyValidatedDateOfBirth);
        when(matchingServiceRequestDto.getMatchingAttributesDto().getAddresses()).thenReturn(addresses);
        when(personDAO.getMatchingUsers(any(), any())).thenReturn(Arrays.asList(person));

        assertThat(cycle1MatchingService.matchUser(matchingServiceRequestDto)).isEqualTo(NO_MATCH);
    }

    @Test
    public void shouldSavePidWhenThereIsMatchOnExactlyOneUserWithSurnameDateOfBirthAndPostcode() {
        Person person = aPerson().withId(1233).withAddress(anAddress().withPostcode("some-postcode").build()).build();
        List<AddressDto> addresses = Arrays.asList(
            anAddressDtoBuilder().withPostCode("some-postcode").withVerified(true).build(),
            anAddressDtoBuilder().withPostCode("another-postcode").withVerified(true).build()
        );

        when(matchingServiceRequestDto.getMatchingAttributesDto().getSurnames()).thenReturn(anyListOfSurnamesWithValidatedValue);
        when(matchingServiceRequestDto.getMatchingAttributesDto().getDateOfBirth()).thenReturn(anyValidatedDateOfBirth);
        when(matchingServiceRequestDto.getMatchingAttributesDto().getAddresses()).thenReturn(addresses);
        when(personDAO.getMatchingUsers(any(), any())).thenReturn(Arrays.asList(person));

        assertThat(cycle1MatchingService.matchUser(matchingServiceRequestDto)).isEqualTo(MATCH);

        verify(verifiedPidDAO, times(1)).save(matchingServiceRequestDto.getHashedPid(), 1233);
    }

    @Test
    public void shouldIgnoreCaseForPostcodeWhenMatching() {
        Person person = aPerson().withId(1233).withAddress(anAddress().withPostcode("Some   Postcode").build()).build();
        List<AddressDto> addresses = Arrays.asList(
            anAddressDtoBuilder().withPostCode("SomePostCode").withVerified(true).build(),
            anAddressDtoBuilder().withPostCode("AnotherPostCode").withVerified(true).build()
        );

        when(matchingServiceRequestDto.getMatchingAttributesDto().getSurnames()).thenReturn(anyListOfSurnamesWithValidatedValue);
        when(matchingServiceRequestDto.getMatchingAttributesDto().getDateOfBirth()).thenReturn(anyValidatedDateOfBirth);
        when(matchingServiceRequestDto.getMatchingAttributesDto().getAddresses()).thenReturn(addresses);
        when(personDAO.getMatchingUsers(any(), any())).thenReturn(Arrays.asList(person));

        assertThat(cycle1MatchingService.matchUser(matchingServiceRequestDto)).isEqualTo(MATCH);
    }

    @Test
    public void shouldReturnNoMatchWhenMultipleRecordsMatchSurnameDateOfBirthAndPostcode() {
        LocalDate dateOfBirth = LocalDate.now().minusYears(10);
        Person personOne = aPerson()
            .withSurname("some-surname")
            .withDateOfBirth(dateOfBirth)
            .withAddress(anAddress().withPostcode("some-postcode").build())
            .build();
        Person personTwo = aPerson()
            .withSurname("some-surname")
            .withDateOfBirth(dateOfBirth)
            .withAddress(anAddress().withPostcode("some-postcode").build())
            .build();
        MatchingAttributesValueDto surnameAttribute = aMatchingAttributesValueDtoBuilder().withValue("some-surname").withVerified(true).build();
        MatchingAttributesValueDto dateOfBirthAttribute = aMatchingAttributesValueDtoBuilder().withValue(dateOfBirth).withVerified(true).build();
        AddressDto address = anAddressDtoBuilder().withPostCode("some-postcode").withVerified(true).build();


        when(matchingServiceRequestDto.getMatchingAttributesDto().getSurnames()).thenReturn(Arrays.asList(surnameAttribute));
        when(matchingServiceRequestDto.getMatchingAttributesDto().getDateOfBirth()).thenReturn(dateOfBirthAttribute);
        when(matchingServiceRequestDto.getMatchingAttributesDto().getAddresses()).thenReturn(Arrays.asList(address));
        when(personDAO.getMatchingUsers(any(), any())).thenReturn(Arrays.asList(personOne, personTwo));

        assertThat(cycle1MatchingService.matchUser(matchingServiceRequestDto)).isEqualTo(NO_MATCH);
    }


    @Test
    public void shouldNotSaveVerifiedPidInTheDatabaseWhenThereIsNoMatchingUser() {
        when(personDAO.getMatchingUsers(any(), any())).thenReturn(null);
        cycle1MatchingService = new Cycle1MatchingService(personDAO, verifiedPidDAO);

        MatchingServiceRequestDto requestWithVerifiedSurname = aMatchingServiceRequestDtoBuilder().build();

        MatchStatusResponseDto matchStatusResponseDto = cycle1MatchingService.matchUser(requestWithVerifiedSurname);

        assertThat(matchStatusResponseDto).isEqualTo(NO_MATCH);
        verify(verifiedPidDAO, never()).save(any(), any());
    }
}