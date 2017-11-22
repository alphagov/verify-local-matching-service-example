package uk.gov.ida.verifylocalmatchingserviceexample.service;

import org.junit.Test;
import uk.gov.ida.verifylocalmatchingserviceexample.contracts.MatchStatusResponseDto;
import uk.gov.ida.verifylocalmatchingserviceexample.dao.VerifiedPidDAO;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class Cycle0MatchingServiceTest {
    private VerifiedPidDAO verifiedPidDAO = mock(VerifiedPidDAO.class);
    private Cycle0MatchingService cycle0MatchingService = new Cycle0MatchingService(verifiedPidDAO);

    @Test
    public void shouldReturnMatchWhenRequestPidExistInDatabase() {
        String pid = "test-pid";
        when(verifiedPidDAO.getVerifiedPID(pid)).thenReturn(pid);

        assertThat(cycle0MatchingService.checkForPid(pid)).isEqualTo(MatchStatusResponseDto.MATCH);
    }

    @Test
    public void shouldReturnNoMatchWhenRequestPidDoesNotExistInDatabase() {
        String pid = "test-pid";
        when(verifiedPidDAO.getVerifiedPID(pid)).thenReturn(null);

        assertThat(cycle0MatchingService.checkForPid(pid)).isEqualTo(MatchStatusResponseDto.NO_MATCH);
    }
}