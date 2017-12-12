package uk.gov.ida.verifylocalmatchingserviceexample.service;

import org.junit.Test;
import uk.gov.ida.verifylocalmatchingserviceexample.contracts.MatchStatusResponseDto;
import uk.gov.ida.verifylocalmatchingserviceexample.dataaccess.VerifiedPidDAO;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class Cycle0MatchingServiceTest {
    private VerifiedPidDAO verifiedPid = mock(VerifiedPidDAO.class);
    private Cycle0MatchingService cycle0MatchingService = new Cycle0MatchingService(verifiedPid);

    @Test
    public void shouldReturnMatchWhenRequestPidExistInDatabase() {
        String pid = "test-pid";
        when(verifiedPid.getVerifiedPid(pid)).thenReturn(Optional.of(pid));

        assertThat(cycle0MatchingService.checkForPid(pid)).isEqualTo(MatchStatusResponseDto.MATCH);
    }

    @Test
    public void shouldReturnNoMatchWhenRequestPidDoesNotExistInDatabase() {
        String pid = "test-pid";
        when(verifiedPid.getVerifiedPid(pid)).thenReturn(Optional.empty());

        assertThat(cycle0MatchingService.checkForPid(pid)).isEqualTo(MatchStatusResponseDto.NO_MATCH);
    }
}