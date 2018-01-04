package uk.gov.ida.verifylocalmatchingserviceexample.service;

import org.junit.Test;
import uk.gov.ida.verifylocalmatchingserviceexample.dataaccess.VerifiedPidDAO;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class Cycle0MatchingServiceTest {
    private VerifiedPidDAO verifiedPid = mock(VerifiedPidDAO.class);
    private Cycle0MatchingService cycle0MatchingService = new Cycle0MatchingService(verifiedPid);

    @Test
    public void shouldReturnPidWhenUserMatches() {
        String pid = "test-pid";
        when(verifiedPid.getVerifiedPid(pid)).thenReturn(Optional.of(pid));

        assertThat(cycle0MatchingService.checkForPid(pid).get()).isEqualTo(pid);
    }

    @Test
    public void shouldReturnNullWhenUserPidDoesNotMatch() {
        String pid = "test-pid";
        when(verifiedPid.getVerifiedPid(pid)).thenReturn(Optional.empty());

        assertThat(cycle0MatchingService.checkForPid(pid)).isEqualTo(Optional.empty());
    }
}