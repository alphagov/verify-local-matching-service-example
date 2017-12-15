package uk.gov.ida.verifylocalmatchingserviceexample.service;

import org.joda.time.DateTime;
import org.junit.Test;
import uk.gov.ida.verifylocalmatchingserviceexample.contracts.MatchStatusResponseDto;
import uk.gov.ida.verifylocalmatchingserviceexample.contracts.MatchingAttributesValueDto;
import uk.gov.ida.verifylocalmatchingserviceexample.contracts.MatchingServiceRequestDto;
import uk.gov.ida.verifylocalmatchingserviceexample.dataaccess.PersonDAO;
import uk.gov.ida.verifylocalmatchingserviceexample.model.Person;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.*;
import static uk.gov.ida.verifylocalmatchingserviceexample.builders.MatchingAttributesDtoBuilder.aMatchingAttributesDtoBuilder;
import static uk.gov.ida.verifylocalmatchingserviceexample.builders.MatchingAttributesValueDtoBuilder.aMatchingAttributesValueDtoBuilder;
import static uk.gov.ida.verifylocalmatchingserviceexample.builders.MatchingServiceRequestDtoBuilder.aMatchingServiceRequestDtoBuilder;

public class Cycle1MatchingServiceTest {
    private PersonDAO personDAO = mock(PersonDAO.class);
    private Cycle1MatchingService cycle1MatchingService;

    @Test
    public void shouldReturnMatchWhenUserWithGivenSurnameAndDateOfBirthIsFound() {
        LinkedList<String> surnames = new LinkedList<String>() {{
            add("test-surname");
        }};
        LocalDate dateOfBirth = LocalDate.of(1993, 12, 16);
        LinkedList<Person> matchingUsers = new LinkedList<Person>() {{
            add(new Person("surname", dateOfBirth));
        }};
        MatchingAttributesValueDto verifiedDateOfBirth = aMatchingAttributesValueDtoBuilder()
                .withVerified(true)
                .withValue(dateOfBirth).build();

        when(personDAO.getMatchingUsers(surnames, dateOfBirth)).thenReturn(matchingUsers);

        cycle1MatchingService = new Cycle1MatchingService(personDAO);
        MatchingServiceRequestDto requestWithVerifiedSurnameAndDateOfBirth = aMatchingServiceRequestDtoBuilder()
                .withMatchingAttributesDto(aMatchingAttributesDtoBuilder()
                        .withSurname(getVerifiedSurname(surnames.getFirst()))
                        .withDateOfBirth(verifiedDateOfBirth)
                        .build())
                .build();

        MatchStatusResponseDto matchStatusResponseDto = cycle1MatchingService.matchUser(requestWithVerifiedSurnameAndDateOfBirth);

        verify(personDAO, times(1)).getMatchingUsers(surnames, dateOfBirth);
        assertThat(matchStatusResponseDto).isEqualTo(MatchStatusResponseDto.MATCH);
    }

    @Test
    public void shouldReturnNoMatchWhenUserWithGivenSurnameAndDateOfBirthNotFoundInTheDatabase() {
        LinkedList<String> surnames = new LinkedList<String>() {{
            add("test-surname");
        }};
        LocalDate dateOfBirth = LocalDate.of(1993, 12, 16);
        MatchingAttributesValueDto verifiedDateOfBirth = aMatchingAttributesValueDtoBuilder()
                .withVerified(true)
                .withValue(dateOfBirth).build();
        when(personDAO.getMatchingUsers(surnames, dateOfBirth)).thenReturn(null);
        cycle1MatchingService = new Cycle1MatchingService(personDAO);

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
        MatchingAttributesValueDto verifiedDateOfBirth = aMatchingAttributesValueDtoBuilder()
                .withVerified(true)
                .withValue(dateOfBirth).build();
        cycle1MatchingService = new Cycle1MatchingService(personDAO);

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
        cycle1MatchingService = new Cycle1MatchingService(personDAO);

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
        cycle1MatchingService = new Cycle1MatchingService(personDAO);

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

        cycle1MatchingService = new Cycle1MatchingService(personDAO);
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
        cycle1MatchingService = new Cycle1MatchingService(personDAO);

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
        cycle1MatchingService = new Cycle1MatchingService(personDAO);
        LinkedList<Person> matchingUsers = new LinkedList<Person>() {{
            add(new Person(verifiedSurnames.get(0), dateOfBirth));
            add(new Person(verifiedSurnames.get(1), dateOfBirth.plusDays(3)));
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