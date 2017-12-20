package uk.gov.ida.verifylocalmatchingserviceexample.service;

import org.junit.Test;
import uk.gov.ida.verifylocalmatchingserviceexample.contracts.MatchStatusResponseDto;
import uk.gov.ida.verifylocalmatchingserviceexample.contracts.MatchingServiceRequestDto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class MatchingServiceTest {

    private Cycle0MatchingService cycle0MatchingService = mock(Cycle0MatchingService.class);
    private Cycle1MatchingService cycle1MatchingService = mock(Cycle1MatchingService.class);
    private MatchingService matchingService = new MatchingService(cycle0MatchingService, cycle1MatchingService);
    private MatchingServiceRequestDto matchingServiceRequestDto = mock(MatchingServiceRequestDto.class);

    @Test
    public void shouldReturnMatchForCycle0WhenPidExists() {
        String hashedPid = "hashedPid";
        when(matchingServiceRequestDto.getHashedPid()).thenReturn(hashedPid);
        when(cycle0MatchingService.checkForPid(hashedPid)).thenReturn(MatchStatusResponseDto.MATCH);

        MatchStatusResponseDto matchStatus = matchingService.findMatchingUser(matchingServiceRequestDto);

        verify(cycle1MatchingService, times(0)).matchUser(any());
        assertThat(matchStatus).isEqualTo(MatchStatusResponseDto.MATCH);
    }

    @Test
    public void shouldReturnMatchForCycle1WhenPidDoesNotExist() {
        String hashedPid = "hashedPid";
        when(matchingServiceRequestDto.getHashedPid()).thenReturn(hashedPid);
        when(cycle0MatchingService.checkForPid(hashedPid)).thenReturn(MatchStatusResponseDto.NO_MATCH);
        when(cycle1MatchingService.matchUser(matchingServiceRequestDto)).thenReturn(MatchStatusResponseDto.MATCH);

        MatchStatusResponseDto matchStatus = matchingService.findMatchingUser(matchingServiceRequestDto);

        verify(cycle1MatchingService, times(1)).matchUser(matchingServiceRequestDto);
        assertThat(matchStatus).isEqualTo(MatchStatusResponseDto.MATCH);
    }
}