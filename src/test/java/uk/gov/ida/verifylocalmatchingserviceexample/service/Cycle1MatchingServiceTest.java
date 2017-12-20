package uk.gov.ida.verifylocalmatchingserviceexample.service;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Answers;
import uk.gov.ida.verifylocalmatchingserviceexample.contracts.MatchStatusResponseDto;
import uk.gov.ida.verifylocalmatchingserviceexample.contracts.MatchingAttributesValueDto;
import uk.gov.ida.verifylocalmatchingserviceexample.contracts.MatchingServiceRequestDto;
import uk.gov.ida.verifylocalmatchingserviceexample.dataaccess.PersonDAO;
import uk.gov.ida.verifylocalmatchingserviceexample.dataaccess.VerifiedPidDAO;
import uk.gov.ida.verifylocalmatchingserviceexample.model.Person;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.ida.verifylocalmatchingserviceexample.builders.AddressBuilder.anyAddress;
import static uk.gov.ida.verifylocalmatchingserviceexample.builders.MatchingAttributesDtoBuilder.aMatchingAttributesDtoBuilder;
import static uk.gov.ida.verifylocalmatchingserviceexample.builders.MatchingAttributesValueDtoBuilder.aMatchingAttributesValueDtoBuilder;
import static uk.gov.ida.verifylocalmatchingserviceexample.builders.MatchingServiceRequestDtoBuilder.aMatchingServiceRequestDtoBuilder;
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
    public void shouldReturnNoMatchWhenThereAreNoUsersWithGivenSurnamesAndDateOfBurth() {
        when(matchingServiceRequestDto.getMatchingAttributesDto().getSurnames()).thenReturn(anyListOfSurnamesWithValidatedValue);
        when(matchingServiceRequestDto.getMatchingAttributesDto().getDateOfBirth()).thenReturn(anyValidatedDateOfBirth);
        when(personDAO.getMatchingUsers(anyListOfSurnamesWithValidatedValue, anyValidatedDateOfBirth.getValue())).thenReturn(emptyList());

        assertThat(cycle1MatchingService.matchUser(matchingServiceRequestDto)).isEqualTo(NO_MATCH);
    }

    @Test
    public void shouldReturnMatchWhenUserWithGivenSurnameAndDateOfBirthIsFound() {
        LinkedList<String> surnames = new LinkedList<String>() {{
            add("test-surname");
        }};
        LocalDate dateOfBirth = LocalDate.of(1993, 12, 16);
        LinkedList<Person> matchingUsers = new LinkedList<Person>() {{
            add(new Person(1233, "surname", dateOfBirth, anyAddress()));
        }};
        MatchingAttributesValueDto verifiedDateOfBirth = aMatchingAttributesValueDtoBuilder()
            .withVerified(true)
            .withValue(dateOfBirth).build();

        when(personDAO.getMatchingUsers(surnames, dateOfBirth)).thenReturn(matchingUsers);

        cycle1MatchingService = new Cycle1MatchingService(personDAO, verifiedPidDAO);
        MatchingServiceRequestDto requestWithVerifiedSurnameAndDateOfBirth = aMatchingServiceRequestDtoBuilder()
            .withMatchingAttributesDto(aMatchingAttributesDtoBuilder()
                .withSurname(getVerifiedSurname(surnames.getFirst()))
                .withDateOfBirth(verifiedDateOfBirth)
                .build())
            .build();

        MatchStatusResponseDto matchStatusResponseDto = cycle1MatchingService.matchUser(requestWithVerifiedSurnameAndDateOfBirth);

        verify(personDAO, times(1)).getMatchingUsers(surnames, dateOfBirth);
        assertThat(matchStatusResponseDto).isEqualTo(MATCH);
    }

    @Test
    public void shouldReturnNoMatchWhenUserWithGivenSurnameAndDateOfBirthNotFoundInTheDatabase() {
        LinkedList<String> surnames = new LinkedList<String>() {{
            add("test-surname");
        }};
        LocalDate dateOfBirth = LocalDate.of(1993, 12, 16);
        MatchingAttributesValueDto verifiedDateOfBirth = getVerifiedDateOfBirth(dateOfBirth);
        when(personDAO.getMatchingUsers(surnames, dateOfBirth)).thenReturn(null);
        cycle1MatchingService = new Cycle1MatchingService(personDAO, verifiedPidDAO);

        MatchingServiceRequestDto requestWithVerifiedSurname = aMatchingServiceRequestDtoBuilder()
            .withMatchingAttributesDto(aMatchingAttributesDtoBuilder()
                .withSurname(getVerifiedSurname(surnames.getFirst()))
                .withDateOfBirth(verifiedDateOfBirth)
                .build())
            .build();

        MatchStatusResponseDto matchStatusResponseDto = cycle1MatchingService.matchUser(requestWithVerifiedSurname);

        verify(personDAO, times(1)).getMatchingUsers(surnames, dateOfBirth);
        assertThat(matchStatusResponseDto).isEqualTo(MatchStatusResponseDto.NO_MATCH);
    }

    @Test
    public void shouldReturnNoMatchWhenRequestHasVerifiedSurnameButIsNotCurrentSurname() {
        LocalDate dateOfBirth = LocalDate.of(1993, 12, 16);
        MatchingAttributesValueDto verifiedDateOfBirth = getVerifiedDateOfBirth(dateOfBirth);
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
        assertThat(matchStatusResponseDto).isEqualTo(MatchStatusResponseDto.NO_MATCH);
    }

    @Test
    public void shouldReturnNoMatchWhenRequestHasCurrentSurnameButNotVerified() {
        LocalDate dateOfBirth = LocalDate.of(1993, 12, 16);
        MatchingAttributesValueDto verifiedDateOfBirth = aMatchingAttributesValueDtoBuilder()
            .withVerified(true)
            .withValue(dateOfBirth).build();
        cycle1MatchingService = new Cycle1MatchingService(personDAO, verifiedPidDAO);

        MatchingServiceRequestDto requestWithCurrentSurnameNotVerified = aMatchingServiceRequestDtoBuilder()
            .withMatchingAttributesDto(aMatchingAttributesDtoBuilder()
                .withSurname(aMatchingAttributesValueDtoBuilder()
                    .withVerified(false)
                    .withTo(null)
                    .withValue("test-surname").build())
                .withDateOfBirth(verifiedDateOfBirth).build())
            .build();
        MatchStatusResponseDto matchStatusResponseDto = cycle1MatchingService.matchUser(requestWithCurrentSurnameNotVerified);

        verify(personDAO, never()).getMatchingUsers(anyList(), any());
        assertThat(matchStatusResponseDto).isEqualTo(MatchStatusResponseDto.NO_MATCH);
    }

    @Test
    public void shouldReturnNoMatchWhenRequestHasUnVerifiedDateOfBirth() {
        LocalDate dateOfBirth = LocalDate.of(1993, 12, 16);
        MatchingAttributesValueDto unVerifiedDateOfBirth = aMatchingAttributesValueDtoBuilder()
            .withVerified(false)
            .withValue(dateOfBirth)
            .build();
        cycle1MatchingService = new Cycle1MatchingService(personDAO, verifiedPidDAO);

        MatchingServiceRequestDto requestWithCurrentSurnameNotVerified = aMatchingServiceRequestDtoBuilder()
            .withMatchingAttributesDto(aMatchingAttributesDtoBuilder()
                .withSurname(getVerifiedSurname("verified surname"))
                .withDateOfBirth(unVerifiedDateOfBirth).build())
            .build();
        MatchStatusResponseDto matchStatusResponseDto = cycle1MatchingService.matchUser(requestWithCurrentSurnameNotVerified);

        verify(personDAO, never()).getMatchingUsers(anyList(), any());
        assertThat(matchStatusResponseDto).isEqualTo(MatchStatusResponseDto.NO_MATCH);
    }

    @Test
    public void shouldCheckAllSurnamesWhenUserHasMultipleCurrentAndVerifiedSurnames() {
        List<String> verifiedAndCurrentSurnames = Arrays.asList("verified 1", "verified 2", "verified 3");
        LocalDate dateOfBirth = LocalDate.of(1993, 12, 16);
        MatchingAttributesValueDto verifiedDateOfBirth = aMatchingAttributesValueDtoBuilder()
            .withVerified(true)
            .withValue(dateOfBirth).build();
        when(personDAO.getMatchingUsers(verifiedAndCurrentSurnames, dateOfBirth)).thenReturn(mock(LinkedList.class));

        cycle1MatchingService = new Cycle1MatchingService(personDAO, verifiedPidDAO);
        MatchingServiceRequestDto requestWithMultipleCurrentAndVerifiedSurnames = aMatchingServiceRequestDtoBuilder()
            .withMatchingAttributesDto(aMatchingAttributesDtoBuilder()
                .withSurnames(getVerifiedSurnames((String[]) verifiedAndCurrentSurnames.toArray()))
                .withDateOfBirth(verifiedDateOfBirth).build())
            .build();

        cycle1MatchingService.matchUser(requestWithMultipleCurrentAndVerifiedSurnames);

        verify(personDAO, times(1)).getMatchingUsers(verifiedAndCurrentSurnames, dateOfBirth);
    }

    @Test
    public void shouldFilterOutUnVerifiedSurnamesWhenUserHasMultipleSurnames() {
        List<String> verifiedSurnames = Arrays.asList("verified 1", "verified 2");
        LocalDate dateOfBirth = LocalDate.of(1993, 12, 16);
        MatchingAttributesValueDto verifiedDateOfBirth = aMatchingAttributesValueDtoBuilder()
            .withVerified(true)
            .withValue(dateOfBirth).build();
        cycle1MatchingService = new Cycle1MatchingService(personDAO, verifiedPidDAO);

        List<MatchingAttributesValueDto<String>> surnames = getVerifiedSurnames((String[]) verifiedSurnames.toArray());
        surnames.add((aMatchingAttributesValueDtoBuilder().withValue("unverified").build()));

        MatchingServiceRequestDto requestWithVerifiedAndUnVerifiedSurnames = aMatchingServiceRequestDtoBuilder()
            .withMatchingAttributesDto(aMatchingAttributesDtoBuilder()
                .withSurnames(surnames)
                .withDateOfBirth(verifiedDateOfBirth)
                .build())
            .build();

        cycle1MatchingService.matchUser(requestWithVerifiedAndUnVerifiedSurnames);

        verify(personDAO, times(1)).getMatchingUsers(verifiedSurnames, dateOfBirth);
    }

    @Test
    public void shouldReturnNoMatchWhenThereAreMultipleUsersMatchingCurrentAndVerifiedSurnameAndDateOfBirth() {
        List<String> verifiedSurnames = Arrays.asList("verified 1", "verified 2");
        LocalDate dateOfBirth = LocalDate.of(1993, 12, 16);
        MatchingAttributesValueDto verifiedDateOfBirth = aMatchingAttributesValueDtoBuilder()
            .withVerified(true)
            .withValue(dateOfBirth).build();
        cycle1MatchingService = new Cycle1MatchingService(personDAO, verifiedPidDAO);
        LinkedList<Person> matchingUsers = new LinkedList<Person>() {{
            add(new Person(13332, verifiedSurnames.get(0), dateOfBirth, anyAddress()));
            add(new Person(1233, verifiedSurnames.get(1), dateOfBirth.plusDays(3), anyAddress()));
        }};
        when(personDAO.getMatchingUsers(verifiedSurnames, dateOfBirth)).thenReturn(matchingUsers);

        MatchingServiceRequestDto requestWithVerifiedSurnames = aMatchingServiceRequestDtoBuilder()
            .withMatchingAttributesDto(aMatchingAttributesDtoBuilder()
                .withSurnames(getVerifiedSurnames((String[]) verifiedSurnames.toArray()))
                .withDateOfBirth(verifiedDateOfBirth)
                .build())
            .build();

        MatchStatusResponseDto matchStatusResponseDto = cycle1MatchingService.matchUser(requestWithVerifiedSurnames);

        assertThat(matchStatusResponseDto).isEqualTo(MatchStatusResponseDto.NO_MATCH);
    }

    @Test
    public void shouldSaveVerifiedPidInTheDatabaseWhenCycle1MatchIsFound() {
        String surname = "test-surname";
        List<String> surnames = singletonList(surname);
        LocalDate dateOfBirth = LocalDate.of(1993, 8, 16);
        int personId = 1233;
        when(personDAO.getMatchingUsers(surnames, dateOfBirth)).thenReturn(getMatchingUsers(surname, dateOfBirth, personId));

        cycle1MatchingService = new Cycle1MatchingService(personDAO, verifiedPidDAO);
        MatchingServiceRequestDto requestWithVerifiedSurnameAndDateOfBirth = aMatchingServiceRequestDtoBuilder()
            .withMatchingAttributesDto(aMatchingAttributesDtoBuilder()
                .withSurname(getVerifiedSurname(surnames.get(0)))
                .withDateOfBirth(getVerifiedDateOfBirth(dateOfBirth))
                .build())
            .build();

        MatchStatusResponseDto matchStatusResponseDto = cycle1MatchingService.matchUser(requestWithVerifiedSurnameAndDateOfBirth);

        assertThat(matchStatusResponseDto).isEqualTo(MatchStatusResponseDto.MATCH);
        verify(verifiedPidDAO, times(1))
            .save(requestWithVerifiedSurnameAndDateOfBirth.getHashedPid(), personId);
    }

    @Test
    public void shouldNotSaveVerifiedPidInTheDatabaseWhenThereIsNoMatchingUser() {
        when(personDAO.getMatchingUsers(any(), any())).thenReturn(null);
        cycle1MatchingService = new Cycle1MatchingService(personDAO, verifiedPidDAO);

        MatchingServiceRequestDto requestWithVerifiedSurname = aMatchingServiceRequestDtoBuilder().build();

        MatchStatusResponseDto matchStatusResponseDto = cycle1MatchingService.matchUser(requestWithVerifiedSurname);

        assertThat(matchStatusResponseDto).isEqualTo(MatchStatusResponseDto.NO_MATCH);
        verify(verifiedPidDAO, never()).save(any(), any());
    }

    private LinkedList<Person> getMatchingUsers(String surname, LocalDate dateOfBirth, int personId) {
        return new LinkedList<Person>() {{
            add(new Person(personId, surname, dateOfBirth, null));
        }};
    }

    private MatchingAttributesValueDto getVerifiedDateOfBirth(LocalDate dateOfBirth) {
        return aMatchingAttributesValueDtoBuilder()
            .withVerified(true)
            .withValue(dateOfBirth).build();
    }

    private MatchingAttributesValueDto<String> getVerifiedSurname(String surname) {
        return aMatchingAttributesValueDtoBuilder()
            .withVerified(true)
            .withValue(surname).build();
    }

    private List<MatchingAttributesValueDto<String>> getVerifiedSurnames(String... surname) {
        return Arrays.stream(surname)
            .map(this::getVerifiedSurname)
            .collect(Collectors.toList());
    }

}