package uk.gov.ida.verifylocalmatchingserviceexample.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Answers;
import uk.gov.ida.verifylocalmatchingserviceexample.contracts.AddressDto;
import uk.gov.ida.verifylocalmatchingserviceexample.contracts.MatchingAttributesValueDto;
import uk.gov.ida.verifylocalmatchingserviceexample.contracts.MatchingServiceRequestDto;
import uk.gov.ida.verifylocalmatchingserviceexample.dataaccess.PersonDAO;
import uk.gov.ida.verifylocalmatchingserviceexample.dataaccess.VerifiedPidDAO;
import uk.gov.ida.verifylocalmatchingserviceexample.model.Person;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.*;
import static uk.gov.ida.verifylocalmatchingserviceexample.builders.AddressBuilder.anAddress;
import static uk.gov.ida.verifylocalmatchingserviceexample.builders.AddressDtoBuilder.anAddressDtoBuilder;
import static uk.gov.ida.verifylocalmatchingserviceexample.builders.MatchingAttributesValueDtoBuilder.aMatchingAttributesValueDtoBuilder;
import static uk.gov.ida.verifylocalmatchingserviceexample.builders.MatchingServiceRequestDtoBuilder.aMatchingServiceRequestDtoBuilder;
import static uk.gov.ida.verifylocalmatchingserviceexample.builders.PersonBuilder.aPerson;
import static uk.gov.ida.verifylocalmatchingserviceexample.builders.PersonBuilder.anyPerson;

public class Cycle1MatchingServiceTest {

    private PersonDAO personDAO;
    private VerifiedPidDAO verifiedPidDAO;
    private Cycle1MatchingService cycle1MatchingService;
    private MatchingServiceRequestDto matchingServiceRequestDto;

    @Before
    public void setUp() {
        personDAO = mock(PersonDAO.class);
        verifiedPidDAO = mock(VerifiedPidDAO.class);
        matchingServiceRequestDto = mock(MatchingServiceRequestDto.class, Answers.RETURNS_DEEP_STUBS);
        cycle1MatchingService = new Cycle1MatchingService(personDAO, verifiedPidDAO);
    }

    @Test
    public void shouldReturnMatchingUserWhenThereIsExactlyOneUserWithSurnameDateOfBirthAndPostcodeAndFirstName() {
        String firstName = "any-firstname";
        Person person = aPerson().withId(1233)
            .withFirstName(firstName)
            .withAddress(anAddress().withPostcode("some-postcode").build()).build();

        when(matchingServiceRequestDto.getMatchingAttributesDto().getSurnames()).thenReturn(getAnyListOfSurnamesWithValidatedValue());
        when(matchingServiceRequestDto.getMatchingAttributesDto().getDateOfBirth()).thenReturn(getAnyVerifiedDateOfBirth());
        when(matchingServiceRequestDto.getMatchingAttributesDto().getAddresses()).thenReturn(getAnyVerifiedAddress());
        when(matchingServiceRequestDto.getMatchingAttributesDto().getFirstName()).thenReturn(getVerifiedFirstName(firstName));
        when(personDAO.getMatchingUsers(any(), any())).thenReturn(Collections.singletonList(person));

        assertThat(cycle1MatchingService.matchUser(matchingServiceRequestDto).get(0)).isEqualTo(person);
    }

    @Test
    public void shouldCheckForMatchingUsersWithVerifiedSurnameAndDateOfBirth() {
        String surname = "verified-surname";
        MatchingAttributesValueDto verifiedSurname = aMatchingAttributesValueDtoBuilder().withValue(surname).withVerified(true).build();
        MatchingAttributesValueDto unverifiedSurname = aMatchingAttributesValueDtoBuilder().withValue("unverified-surname").withVerified(false).build();
        MatchingAttributesValueDto<LocalDate> dateOfBirth = getAnyVerifiedDateOfBirth();

        when(matchingServiceRequestDto.getMatchingAttributesDto().getSurnames()).thenReturn(Arrays.asList(verifiedSurname, unverifiedSurname));
        when(matchingServiceRequestDto.getMatchingAttributesDto().getDateOfBirth()).thenReturn(dateOfBirth);
        when(matchingServiceRequestDto.getMatchingAttributesDto().getAddresses()).thenReturn(getAnyVerifiedAddress());
        when(matchingServiceRequestDto.getMatchingAttributesDto().getFirstName()).thenReturn(getVerifiedFirstName("any-firstname"));

        cycle1MatchingService.matchUser(matchingServiceRequestDto);

        verify(personDAO, times(1)).getMatchingUsers(Collections.singletonList(surname), dateOfBirth.getValue());
    }

    @Test
    public void shouldReturnEmptyListOfMatchingUsersWhenDateOfBirthIsNotVerified() {
        MatchingAttributesValueDto<LocalDate> dateOfBirth = matchingServiceRequestDto.getMatchingAttributesDto().getDateOfBirth();

        when(dateOfBirth.getVerified()).thenReturn(false);
        when(matchingServiceRequestDto.getMatchingAttributesDto().getSurnames()).thenReturn(getAnyListOfSurnamesWithValidatedValue());

        assertThat(cycle1MatchingService.matchUser(matchingServiceRequestDto).isEmpty()).isTrue();
        verify(personDAO, never()).getMatchingUsers(any(), any());
    }

    @Test
    public void shouldReturnEmptyListOfMatchingUsersWhenThereAreNoVerifiedSurnames() {
        List unverifiedSurnameAttributeValues = Collections.singletonList(
            aMatchingAttributesValueDtoBuilder().withValue("some-surname-one").withVerified(false).build()
        );

        when(matchingServiceRequestDto.getMatchingAttributesDto().getSurnames()).thenReturn(unverifiedSurnameAttributeValues);
        when(matchingServiceRequestDto.getMatchingAttributesDto().getDateOfBirth()).thenReturn(getAnyVerifiedDateOfBirth());

        assertThat(cycle1MatchingService.matchUser(matchingServiceRequestDto).isEmpty()).isTrue();
    }

    @Test
    public void shouldReturnEmptyListOfMatchingUsersWhenThereIsNoVerifiedFirstName() {
        MatchingAttributesValueDto unverifiedFirstName = aMatchingAttributesValueDtoBuilder()
            .withValue("unverified-firstname").withVerified(false).build();

        when(matchingServiceRequestDto.getMatchingAttributesDto().getSurnames()).thenReturn(getAnyListOfSurnamesWithValidatedValue());
        when(matchingServiceRequestDto.getMatchingAttributesDto().getDateOfBirth()).thenReturn(getAnyVerifiedDateOfBirth());
        when(matchingServiceRequestDto.getMatchingAttributesDto().getAddresses()).thenReturn(getAnyVerifiedAddress());
        when(matchingServiceRequestDto.getMatchingAttributesDto().getFirstName()).thenReturn(unverifiedFirstName);
        when(personDAO.getMatchingUsers(any(), any())).thenReturn(Collections.singletonList(anyPerson()));

        assertThat(cycle1MatchingService.matchUser(matchingServiceRequestDto).isEmpty()).isTrue();
    }

    @Test
    public void shouldReturnNoMatchWhenThereIsNoFirstNameInMatchingRequest() {
        when(matchingServiceRequestDto.getMatchingAttributesDto().getSurnames()).thenReturn(getAnyListOfSurnamesWithValidatedValue());
        when(matchingServiceRequestDto.getMatchingAttributesDto().getDateOfBirth()).thenReturn(getAnyVerifiedDateOfBirth());
        when(matchingServiceRequestDto.getMatchingAttributesDto().getAddresses()).thenReturn(getAnyVerifiedAddress());
        when(matchingServiceRequestDto.getMatchingAttributesDto().getFirstName()).thenReturn(null);
        when(personDAO.getMatchingUsers(any(), any())).thenReturn(Collections.singletonList(anyPerson()));

        assertThat(cycle1MatchingService.matchUser(matchingServiceRequestDto).isEmpty()).isTrue();
    }

    @Test
    public void shouldReturnEmptyListOfMatchingUsersWhenThereAreNoUsersWithGivenSurnamesAndDateOfBirth() {
        List anyListOfSurnamesWithValidatedValue = getAnyListOfSurnamesWithValidatedValue();
        when(matchingServiceRequestDto.getMatchingAttributesDto().getSurnames()).thenReturn(anyListOfSurnamesWithValidatedValue);
        MatchingAttributesValueDto<LocalDate> anyValidatedDateOfBirth = getAnyVerifiedDateOfBirth();
        when(matchingServiceRequestDto.getMatchingAttributesDto().getDateOfBirth()).thenReturn(anyValidatedDateOfBirth);
        when(personDAO.getMatchingUsers(anyListOfSurnamesWithValidatedValue, anyValidatedDateOfBirth.getValue())).thenReturn(emptyList());

        assertThat(cycle1MatchingService.matchUser(matchingServiceRequestDto).isEmpty()).isTrue();
    }

    @Test
    public void shouldReturnMatchingUserWithVerifiedFirstNameWhenThereIsNoVerifiedPostCode() {
        List<AddressDto> unverifiedPostcodeAttributeValues = Collections.singletonList(
            anAddressDtoBuilder().withVerified(false).build()
        );
        String firstName = "any-firstname";
        Person person = aPerson().withId(1233)
            .withFirstName(firstName)
            .withAddress(anAddress().withPostcode("random-postcode").build()).build();

        when(matchingServiceRequestDto.getMatchingAttributesDto().getSurnames()).thenReturn(getAnyListOfSurnamesWithValidatedValue());
        when(matchingServiceRequestDto.getMatchingAttributesDto().getDateOfBirth()).thenReturn(getAnyVerifiedDateOfBirth());
        when(matchingServiceRequestDto.getMatchingAttributesDto().getAddresses()).thenReturn(unverifiedPostcodeAttributeValues);
        when(matchingServiceRequestDto.getMatchingAttributesDto().getFirstName()).thenReturn(getVerifiedFirstName(firstName));
        when(personDAO.getMatchingUsers(any(), any())).thenReturn(Collections.singletonList(person));

        assertThat(cycle1MatchingService.matchUser(matchingServiceRequestDto)).isEqualTo(Collections.singletonList(person));
    }

    @Test
    public void shouldReturnUserWithMatchingFirstNameWhenMultipleRecordsMatchWithVerifiedPostCode() {
        LocalDate dateOfBirth = LocalDate.now().minusYears(10);
        String firstName = "random-first-name";
        Person userWithNotMatchingFirstName = aPerson()
            .withSurname("some-surname")
            .withFirstName("not-matching-first-name")
            .withDateOfBirth(dateOfBirth)
            .withAddress(anAddress().withPostcode("matching-postcode").build())
            .build();
        Person userWithMatchingFirstName = aPerson()
            .withSurname("some-surname")
            .withFirstName(firstName)
            .withDateOfBirth(dateOfBirth)
            .withAddress(anAddress().withPostcode("matching-postcode").build())
            .build();
        List<AddressDto> addresses = Collections.singletonList(
            anAddressDtoBuilder().withPostCode("matching-postcode").withVerified(true).build()
        );

        when(matchingServiceRequestDto.getMatchingAttributesDto().getSurnames()).thenReturn(getAnyListOfSurnamesWithValidatedValue());
        when(matchingServiceRequestDto.getMatchingAttributesDto().getDateOfBirth()).thenReturn(getAnyVerifiedDateOfBirth());
        when(matchingServiceRequestDto.getMatchingAttributesDto().getAddresses()).thenReturn(addresses);
        when(matchingServiceRequestDto.getMatchingAttributesDto().getFirstName()).thenReturn(getVerifiedFirstName(firstName));
        when(personDAO.getMatchingUsers(any(), any())).thenReturn(Arrays.asList(userWithNotMatchingFirstName, userWithMatchingFirstName));

        assertThat(cycle1MatchingService.matchUser(matchingServiceRequestDto).get(0)).isEqualTo(userWithMatchingFirstName);
    }

    @Test
    public void shouldReturnEmptyListOfMatchingUsersWhenHistoricalVerifiedPostCodesDoNotMatch() {
        String firstName = "random-first-name";
        Person person = aPerson()
            .withFirstName(firstName)
            .withAddress(anAddress().withPostcode("some-postcode").build()).build();
        List<AddressDto> addresses = Arrays.asList(
            anAddressDtoBuilder().withPostCode("non-matching-postcode-one").withVerified(true).build(),
            anAddressDtoBuilder().withPostCode("non-matching-postcode-two").withVerified(true).build()
        );

        when(matchingServiceRequestDto.getMatchingAttributesDto().getSurnames()).thenReturn(getAnyListOfSurnamesWithValidatedValue());
        when(matchingServiceRequestDto.getMatchingAttributesDto().getDateOfBirth()).thenReturn(getAnyVerifiedDateOfBirth());
        when(matchingServiceRequestDto.getMatchingAttributesDto().getAddresses()).thenReturn(addresses);
        when(matchingServiceRequestDto.getMatchingAttributesDto().getFirstName()).thenReturn(getVerifiedFirstName(firstName));
        when(personDAO.getMatchingUsers(any(), any())).thenReturn(Collections.singletonList(person));

        assertThat(cycle1MatchingService.matchUser(matchingServiceRequestDto).isEmpty()).isTrue();
    }

    @Test
    public void shouldIgnoreCaseAndSpacesForPostcodeWhenMatching() {
        String firstName = "any-firstname";
        Person person = aPerson().withId(1233)
            .withFirstName(firstName)
            .withAddress(anAddress().withPostcode("Some   Postcode").build()).build();
        List<AddressDto> addresses = Arrays.asList(
            anAddressDtoBuilder().withPostCode("SomePostCode").withVerified(true).build(),
            anAddressDtoBuilder().withPostCode("AnotherPostCode").withVerified(true).build()
        );

        when(matchingServiceRequestDto.getMatchingAttributesDto().getSurnames()).thenReturn(getAnyListOfSurnamesWithValidatedValue());
        when(matchingServiceRequestDto.getMatchingAttributesDto().getDateOfBirth()).thenReturn(getAnyVerifiedDateOfBirth());
        when(matchingServiceRequestDto.getMatchingAttributesDto().getAddresses()).thenReturn(addresses);
        when(matchingServiceRequestDto.getMatchingAttributesDto().getFirstName()).thenReturn(getVerifiedFirstName(firstName));
        when(personDAO.getMatchingUsers(any(), any())).thenReturn(Collections.singletonList(person));

        assertThat(cycle1MatchingService.matchUser(matchingServiceRequestDto).get(0)).isEqualTo(person);
    }

    @Test
    public void shouldIgnoreCaseAndSpacesForFirstNameWhenMatching() {
        Person person = aPerson().withId(1233)
            .withFirstName("Ann marie")
            .withAddress(anAddress().withPostcode("some-postcode").build()).build();

        when(matchingServiceRequestDto.getMatchingAttributesDto().getSurnames()).thenReturn(getAnyListOfSurnamesWithValidatedValue());
        when(matchingServiceRequestDto.getMatchingAttributesDto().getDateOfBirth()).thenReturn(getAnyVerifiedDateOfBirth());
        when(matchingServiceRequestDto.getMatchingAttributesDto().getAddresses()).thenReturn(getAnyVerifiedAddress());
        when(matchingServiceRequestDto.getMatchingAttributesDto().getFirstName()).thenReturn(getVerifiedFirstName("AnnMarie"));
        when(personDAO.getMatchingUsers(any(), any())).thenReturn(Collections.singletonList(person));

        assertThat(cycle1MatchingService.matchUser(matchingServiceRequestDto).get(0)).isEqualTo(person);
    }

    @Test
    public void shouldSavePidWhenThereIsMatchOnExactlyOneUser() {
        String firstName = "any-first-name";
        Person matchingPerson = aPerson()
            .withId(1233)
            .withFirstName(firstName)
            .withAddress(anAddress().withPostcode("some-postcode").build()).build();
        Person nonMatchingPerson = aPerson()
            .withId(1234)
            .withFirstName("random-first-name")
            .withAddress(anAddress().withPostcode("some-postcode").build()).build();

        when(matchingServiceRequestDto.getHashedPid()).thenReturn("random-hashed-pid");
        when(matchingServiceRequestDto.getMatchingAttributesDto().getSurnames()).thenReturn(getAnyListOfSurnamesWithValidatedValue());
        when(matchingServiceRequestDto.getMatchingAttributesDto().getDateOfBirth()).thenReturn(getAnyVerifiedDateOfBirth());
        when(matchingServiceRequestDto.getMatchingAttributesDto().getAddresses()).thenReturn(getAnyVerifiedAddress());
        when(matchingServiceRequestDto.getMatchingAttributesDto().getFirstName()).thenReturn(getVerifiedFirstName(firstName));
        when(personDAO.getMatchingUsers(any(), any())).thenReturn(Arrays.asList(matchingPerson, nonMatchingPerson));

        cycle1MatchingService.matchUser(matchingServiceRequestDto);

        verify(verifiedPidDAO, times(1)).save(matchingServiceRequestDto.getHashedPid(), matchingPerson.getPersonId());
    }

    @Test
    public void shouldNotSaveVerifiedPidInTheDatabaseWhenThereIsNoMatchingUser() {
        when(personDAO.getMatchingUsers(any(), any())).thenReturn(emptyList());

        MatchingServiceRequestDto requestWithVerifiedSurname = aMatchingServiceRequestDtoBuilder().build();

        List<Person> matchingUsers = cycle1MatchingService.matchUser(requestWithVerifiedSurname);

        assertThat(matchingUsers.isEmpty()).isTrue();
        verify(verifiedPidDAO, never()).save(any(), any());
    }

    private MatchingAttributesValueDto getAnyVerifiedDateOfBirth() {
        return aMatchingAttributesValueDtoBuilder().withValue(LocalDate.now()).withVerified(true).build();
    }

    private List getAnyListOfSurnamesWithValidatedValue() {
        return Arrays.asList(
            aMatchingAttributesValueDtoBuilder().withValue("any-surname-one").withVerified(true).build(),
            aMatchingAttributesValueDtoBuilder().withValue("any-surname-two").withVerified(true).build(),
            aMatchingAttributesValueDtoBuilder().withValue("any-surname-three").withVerified(false).build()
        );
    }

    private List<AddressDto> getAnyVerifiedAddress() {
        return Arrays.asList(
            anAddressDtoBuilder().withPostCode("some-postcode").withVerified(true).build(),
            anAddressDtoBuilder().withPostCode("another-postcode").withVerified(true).build()
        );
    }

    private MatchingAttributesValueDto<String> getVerifiedFirstName(String firstName) {
        return aMatchingAttributesValueDtoBuilder().withValue(firstName).withVerified(true).build();
    }
}