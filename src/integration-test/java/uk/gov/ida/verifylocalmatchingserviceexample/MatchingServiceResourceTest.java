package uk.gov.ida.verifylocalmatchingserviceexample;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.dropwizard.testing.junit.DropwizardAppRule;
import org.junit.After;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import uk.gov.ida.verifylocalmatchingserviceexample.configuration.VerifyLocalMatchingServiceExampleConfiguration;
import uk.gov.ida.verifylocalmatchingserviceexample.contracts.MatchStatusResponseDto;
import uk.gov.ida.verifylocalmatchingserviceexample.contracts.MatchingServiceRequestDto;
import uk.gov.ida.verifylocalmatchingserviceexample.rules.H2JDBIRule;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;

import static io.dropwizard.testing.ResourceHelpers.resourceFilePath;
import static org.junit.Assert.assertEquals;
import static uk.gov.ida.verifylocalmatchingserviceexample.builders.MatchingServiceRequestDtoBuilder.aMatchingServiceRequestDtoBuilder;

public class MatchingServiceResourceTest {
    private static final DropwizardAppRule<VerifyLocalMatchingServiceExampleConfiguration> APP_RULE =
            new DropwizardAppRule<>(VerifyLocalMatchingServiceExampleApplication.class,
                    resourceFilePath("verify-local-matching-service-test.yml"));
    private static H2JDBIRule h2JDBIRule = new H2JDBIRule(APP_RULE);

    @ClassRule
    public static RuleChain chain = RuleChain.outerRule(APP_RULE).around(h2JDBIRule);

    @Test
    public void shouldReturnMatchWhenVerifiedPidIsFoundInCycle0Scenario() throws IOException {
        String verifiedPid = "some random string";
        h2JDBIRule.ensurePidExist(verifiedPid);
        MatchingServiceRequestDto matchingServiceRequestDto = aMatchingServiceRequestDtoBuilder()
                .withHashedPid(verifiedPid)
                .build();

        Response response = APP_RULE.client()
                .target(String.format("http://localhost:%d/match-user", APP_RULE.getLocalPort()))
                .request()
                .post(Entity.entity(getRequestString(matchingServiceRequestDto), MediaType.APPLICATION_JSON_TYPE));

        assertEquals(200, response.getStatus());
        assertEquals(MatchStatusResponseDto.MATCH, response.readEntity(MatchStatusResponseDto.class));
    }

    @Test
    public void shouldReturnNoMatchWhenPidNotFoundInCycle0Scenario() throws IOException {
        String verifiedPid = "some random string";
        h2JDBIRule.ensurePidDoesNotExist(verifiedPid);
        MatchingServiceRequestDto matchingServiceRequestDto = aMatchingServiceRequestDtoBuilder()
                .withHashedPid(verifiedPid)
                .build();

        Response response = APP_RULE.client()
                .target(String.format("http://localhost:%d/match-user", APP_RULE.getLocalPort()))
                .request()
                .post(Entity.entity(getRequestString(matchingServiceRequestDto), MediaType.APPLICATION_JSON_TYPE));

        assertEquals(200, response.getStatus());
        assertEquals(MatchStatusResponseDto.NO_MATCH, response.readEntity(MatchStatusResponseDto.class));
    }

    private String getRequestString(MatchingServiceRequestDto matchingServiceRequestDto) throws JsonProcessingException {
        return APP_RULE.getObjectMapper().writeValueAsString(matchingServiceRequestDto);
    }
}
