package uk.gov.ida.verifylocalmatchingserviceexample.service;

import org.junit.Test;
import org.mockito.Answers;
import uk.gov.ida.verifylocalmatchingserviceexample.contracts.MatchStatusResponseDto;
import uk.gov.ida.verifylocalmatchingserviceexample.contracts.MatchingServiceRequestDto;
import uk.gov.ida.verifylocalmatchingserviceexample.model.Person;

import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static uk.gov.ida.verifylocalmatchingserviceexample.builders.PersonBuilder.anyPerson;

public class MatchingServiceTest {

    private Cycle0MatchingService cycle0MatchingService = mock(Cycle0MatchingService.class);
    private Cycle1MatchingService cycle1MatchingService = mock(Cycle1MatchingService.class, Answers.RETURNS_DEEP_STUBS);
    private Cycle3MatchingService cycle3MatchingService = mock(Cycle3MatchingService.class);
    private MatchingService matchingService = new MatchingService(cycle0MatchingService, cycle1MatchingService, cycle3MatchingService);
    private MatchingServiceRequestDto matchingServiceRequestDto = mock(MatchingServiceRequestDto.class);

    @Test
    public void shouldReturnMatchForCycle0WhenPidExists() {
        String hashedPid = "hashedPid";
        when(matchingServiceRequestDto.getHashedPid()).thenReturn(hashedPid);
        when(cycle0MatchingService.checkForPid(hashedPid)).thenReturn(Optional.of("some-pid"));

        MatchStatusResponseDto matchStatus = matchingService.findMatchingUser(matchingServiceRequestDto);

        verify(cycle1MatchingService, never()).matchUser(any());
        assertThat(matchStatus).isEqualTo(MatchStatusResponseDto.MATCH);
    }

    @Test
    public void shouldCheckForCycle1WhenPidDoesNotExistInCycle0() {
        String hashedPid = "hashedPid";
        when(matchingServiceRequestDto.getHashedPid()).thenReturn(hashedPid);
        when(cycle0MatchingService.checkForPid(hashedPid)).thenReturn(Optional.empty());

        MatchStatusResponseDto matchStatus = matchingService.findMatchingUser(matchingServiceRequestDto);

        verify(cycle1MatchingService, times(1)).matchUser(any());
    }

    @Test
    public void shouldReturnMatchWhenExactlyOneUserMatchInCycle1() {
        String hashedPid = "hashedPid";
        when(matchingServiceRequestDto.getHashedPid()).thenReturn(hashedPid);
        when(cycle0MatchingService.checkForPid(hashedPid)).thenReturn(Optional.empty());
        when(cycle1MatchingService.matchUser(matchingServiceRequestDto)).thenReturn(singletonList(anyPerson()));

        MatchStatusResponseDto matchStatus = matchingService.findMatchingUser(matchingServiceRequestDto);

        verify(cycle1MatchingService, times(1)).matchUser(matchingServiceRequestDto);
        assertThat(matchStatus).isEqualTo(MatchStatusResponseDto.MATCH);
    }

    @Test
    public void shouldReturnNoMatchWhenCycle1DoesNotMatchAnyUser() {
        String hashedPid = "hashedPid";
        when(matchingServiceRequestDto.getHashedPid()).thenReturn(hashedPid);
        when(cycle0MatchingService.checkForPid(hashedPid)).thenReturn(Optional.empty());
        List<Person> matchingUsers = asList(anyPerson(), anyPerson());
        when(cycle1MatchingService.matchUser(matchingServiceRequestDto)).thenReturn(emptyList());

        MatchStatusResponseDto matchStatus = matchingService.findMatchingUser(matchingServiceRequestDto);

        assertThat(matchStatus).isEqualTo(MatchStatusResponseDto.NO_MATCH);
    }

    @Test
    public void shouldCheckForCycle3WhenCycle1MatchesMultipleUsers() {
        String hashedPid = "hashedPid";
        when(matchingServiceRequestDto.getHashedPid()).thenReturn(hashedPid);
        when(cycle0MatchingService.checkForPid(hashedPid)).thenReturn(Optional.empty());
        List<Person> matchingUsers = asList(anyPerson(), anyPerson());
        when(cycle1MatchingService.matchUser(matchingServiceRequestDto)).thenReturn(matchingUsers);
        when(cycle3MatchingService.matchUser(matchingServiceRequestDto, matchingUsers)).thenReturn(singletonList(1));

        matchingService.findMatchingUser(matchingServiceRequestDto);

        verify(cycle3MatchingService, times(1)).matchUser(any(), anyList());
    }

    @Test
    public void shouldReturnMatchWhenCycle3MatchesOneUser() {
        String hashedPid = "hashedPid";
        when(matchingServiceRequestDto.getHashedPid()).thenReturn(hashedPid);
        when(cycle0MatchingService.checkForPid(hashedPid)).thenReturn(Optional.empty());
        List<Person> matchingUsers = asList(anyPerson(), anyPerson());
        when(cycle1MatchingService.matchUser(matchingServiceRequestDto)).thenReturn(matchingUsers);
        when(cycle3MatchingService.matchUser(matchingServiceRequestDto, matchingUsers)).thenReturn(singletonList(1));

        MatchStatusResponseDto matchStatus = matchingService.findMatchingUser(matchingServiceRequestDto);

        assertThat(matchStatus).isEqualTo(MatchStatusResponseDto.MATCH);
    }

    @Test
    public void shouldReturnNoMatchWhenCycle3DoesNotMatchAnyUser() {
        String hashedPid = "hashedPid";
        when(matchingServiceRequestDto.getHashedPid()).thenReturn(hashedPid);
        when(cycle0MatchingService.checkForPid(hashedPid)).thenReturn(Optional.empty());
        List<Person> matchingUsers = asList(anyPerson(), anyPerson());
        when(cycle1MatchingService.matchUser(matchingServiceRequestDto)).thenReturn(matchingUsers);
        when(cycle3MatchingService.matchUser(matchingServiceRequestDto, matchingUsers)).thenReturn(emptyList());

        MatchStatusResponseDto matchStatus = matchingService.findMatchingUser(matchingServiceRequestDto);

        assertThat(matchStatus).isEqualTo(MatchStatusResponseDto.NO_MATCH);
    }

    @Test
    public void shouldReturnNoMatchWhenCycle3MatchesMultipleUsers() {
        String hashedPid = "hashedPid";
        when(matchingServiceRequestDto.getHashedPid()).thenReturn(hashedPid);
        when(cycle0MatchingService.checkForPid(hashedPid)).thenReturn(Optional.empty());
        List<Person> matchingUsers = asList(anyPerson(), anyPerson());
        when(cycle1MatchingService.matchUser(matchingServiceRequestDto)).thenReturn(matchingUsers);
        when(cycle3MatchingService.matchUser(matchingServiceRequestDto, matchingUsers)).thenReturn(asList(1,2,3));

        MatchStatusResponseDto matchStatus = matchingService.findMatchingUser(matchingServiceRequestDto);

        assertThat(matchStatus).isEqualTo(MatchStatusResponseDto.NO_MATCH);
    }
}