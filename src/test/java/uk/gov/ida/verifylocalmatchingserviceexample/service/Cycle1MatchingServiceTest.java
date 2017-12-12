package uk.gov.ida.verifylocalmatchingserviceexample.service;

import org.joda.time.DateTime;
import org.junit.Test;
import uk.gov.ida.verifylocalmatchingserviceexample.contracts.MatchStatusResponseDto;
import uk.gov.ida.verifylocalmatchingserviceexample.contracts.MatchingAttributesValueDto;
import uk.gov.ida.verifylocalmatchingserviceexample.contracts.MatchingServiceRequestDto;
import uk.gov.ida.verifylocalmatchingserviceexample.dataaccess.PersonDAO;
import uk.gov.ida.verifylocalmatchingserviceexample.model.Person;

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
    public void shouldReturnMatchWhenUserWithGivenSurnameIsFound() {
        LinkedList<String> surnames = new LinkedList<String>(){{add("test-surname");}};
        LinkedList<Person> matchingUsers = new LinkedList<Person>() {{
            add(new Person("surname"));
        }};
        when(personDAO.getMatchingUsers(surnames)).thenReturn(matchingUsers);

        cycle1MatchingService = new Cycle1MatchingService(personDAO);
        MatchingServiceRequestDto requestWithVerifiedSurname = aMatchingServiceRequestDtoBuilder()
                .withMatchingAttributesDto(aMatchingAttributesDtoBuilder()
                        .withSurname(getVerifiedSurname(surnames.getFirst())).build())
                .build();

        MatchStatusResponseDto matchStatusResponseDto = cycle1MatchingService.matchUser(requestWithVerifiedSurname);

        verify(personDAO, times(1)).getMatchingUsers(surnames);
        assertThat(matchStatusResponseDto).isEqualTo(MatchStatusResponseDto.MATCH);
    }

    @Test
    public void shouldReturnNoMatchWhenUserWithGivenSurnameIsNotFoundInTheDatabase() {
        LinkedList<String> surnames = new LinkedList<String>(){{add("test-surname");}};
        when(personDAO.getMatchingUsers(surnames)).thenReturn(null);
        cycle1MatchingService = new Cycle1MatchingService(personDAO);

        MatchingServiceRequestDto requestWithVerifiedSurname = aMatchingServiceRequestDtoBuilder()
                .withMatchingAttributesDto(aMatchingAttributesDtoBuilder()
                        .withSurname(getVerifiedSurname(surnames.getFirst())).build())
                .build();
        MatchStatusResponseDto matchStatusResponseDto = cycle1MatchingService.matchUser(
                requestWithVerifiedSurname);

        verify(personDAO, times(1)).getMatchingUsers(surnames);
        assertThat(matchStatusResponseDto).isEqualTo(MatchStatusResponseDto.NO_MATCH);
    }

    @Test
    public void shouldReturnNoMatchWhenRequestHasVerifiedSurnameButIsNotCurrentSurname() {
        cycle1MatchingService = new Cycle1MatchingService(personDAO);

        MatchingServiceRequestDto requestWithVerifiedButNotCurrentSurname = aMatchingServiceRequestDtoBuilder()
                .withMatchingAttributesDto(aMatchingAttributesDtoBuilder().withSurname(
                        aMatchingAttributesValueDtoBuilder()
                                .withTo(DateTime.now())
                                .withValue("test-surname").build()).build())
                .build();
        MatchStatusResponseDto matchStatusResponseDto = cycle1MatchingService.matchUser(
                requestWithVerifiedButNotCurrentSurname);

        verify(personDAO, never()).getMatchingUsers(anyList());
        assertThat(matchStatusResponseDto).isEqualTo(MatchStatusResponseDto.NO_MATCH);
    }

    @Test
    public void shouldReturnNoMatchWhenRequestHWithCurrentSurnameButNotVerified() {
        cycle1MatchingService = new Cycle1MatchingService(personDAO);

        MatchingServiceRequestDto requestWithCurrentSurnameNotVerified = aMatchingServiceRequestDtoBuilder().build();
        MatchStatusResponseDto matchStatusResponseDto = cycle1MatchingService.matchUser(requestWithCurrentSurnameNotVerified);

        verify(personDAO, never()).getMatchingUsers(anyList());
        assertThat(matchStatusResponseDto).isEqualTo(MatchStatusResponseDto.NO_MATCH);
    }

    @Test
    public void shouldCheckForMultipleSurnamesWhenUserHasMultipleCurrentAndVerifiedSurnames() {
        List<String> verifiedAndCurrentSurnames = Arrays.asList("verified 1", "verified 2", "verified 3");
        when(personDAO.getMatchingUsers(verifiedAndCurrentSurnames)).thenReturn(mock(LinkedList.class));

        cycle1MatchingService = new Cycle1MatchingService(personDAO);
        MatchingServiceRequestDto requestWithMultipleCurrentAndVerifiedSurnames = aMatchingServiceRequestDtoBuilder()
                .withMatchingAttributesDto(aMatchingAttributesDtoBuilder()
                        .withSurnames(getVerifiedSurnames((String[]) verifiedAndCurrentSurnames.toArray())).build())
                .build();

        cycle1MatchingService.matchUser(requestWithMultipleCurrentAndVerifiedSurnames);

        verify(personDAO, times(1)).getMatchingUsers(verifiedAndCurrentSurnames);
    }

    @Test
    public void shouldFilterOutUnVerifiedSurnamesWhenUserHasMultipleSurnames() {
        List<String> verifiedSurnames = Arrays.asList("verified 1", "verified 2");
        cycle1MatchingService = new Cycle1MatchingService(personDAO);

        List<MatchingAttributesValueDto<String>> surnames = getVerifiedSurnames((String[]) verifiedSurnames.toArray());
        surnames.add((aMatchingAttributesValueDtoBuilder().withValue("unverified").build()));

        MatchingServiceRequestDto requestWithVerifiedAndUnVerifiedSurnames = aMatchingServiceRequestDtoBuilder()
                .withMatchingAttributesDto(aMatchingAttributesDtoBuilder()
                        .withSurnames(surnames).build())
                .build();

        cycle1MatchingService.matchUser(requestWithVerifiedAndUnVerifiedSurnames);

        verify(personDAO, times(1)).getMatchingUsers(verifiedSurnames);
    }

    @Test
    public void shouldReturnNoMatchWhenThereAreMultipleUsersMatchingCurrentAndVerifiedSurname() {
        List<String> verifiedSurnames = Arrays.asList("verified 1", "verified 2");
        cycle1MatchingService = new Cycle1MatchingService(personDAO);
        LinkedList<Person> matchingUsers = new LinkedList<Person>() {{
            add(new Person(verifiedSurnames.get(0)));
            add(new Person(verifiedSurnames.get(1)));
        }};
        when(personDAO.getMatchingUsers(verifiedSurnames)).thenReturn(matchingUsers);

        MatchingServiceRequestDto requestWithVerifiedSurnames = aMatchingServiceRequestDtoBuilder()
                .withMatchingAttributesDto(aMatchingAttributesDtoBuilder()
                        .withSurnames(getVerifiedSurnames((String[]) verifiedSurnames.toArray())).build())
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