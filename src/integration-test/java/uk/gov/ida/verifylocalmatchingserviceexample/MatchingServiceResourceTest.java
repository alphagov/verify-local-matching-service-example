package uk.gov.ida.verifylocalmatchingserviceexample;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.dropwizard.testing.junit.DropwizardAppRule;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import uk.gov.ida.verifylocalmatchingserviceexample.configuration.VerifyLocalMatchingServiceExampleConfiguration;
import uk.gov.ida.verifylocalmatchingserviceexample.contracts.MatchStatusResponseDto;
import uk.gov.ida.verifylocalmatchingserviceexample.contracts.MatchingAttributesValueDto;
import uk.gov.ida.verifylocalmatchingserviceexample.contracts.MatchingServiceRequestDto;
import uk.gov.ida.verifylocalmatchingserviceexample.rules.TestDatabaseRule;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.LocalDate;

import static io.dropwizard.testing.ResourceHelpers.resourceFilePath;
import static org.junit.Assert.assertEquals;
import static uk.gov.ida.verifylocalmatchingserviceexample.builders.MatchingAttributesDtoBuilder.aMatchingAttributesDtoBuilder;
import static uk.gov.ida.verifylocalmatchingserviceexample.builders.MatchingAttributesValueDtoBuilder.aMatchingAttributesValueDtoBuilder;
import static uk.gov.ida.verifylocalmatchingserviceexample.builders.MatchingServiceRequestDtoBuilder.aMatchingServiceRequestDtoBuilder;

public class MatchingServiceResourceTest {
    private static final DropwizardAppRule<VerifyLocalMatchingServiceExampleConfiguration> APP_RULE =
            new DropwizardAppRule<>(VerifyLocalMatchingServiceExampleApplication.class,
                    resourceFilePath("verify-local-matching-service-test.yml"));

    private static TestDatabaseRule testDatabaseRule = new TestDatabaseRule(APP_RULE);

    @ClassRule
    public static RuleChain chain = RuleChain.outerRule(APP_RULE).around(testDatabaseRule);

    @Test
    public void shouldReturnMatchWhenVerifiedPidIsFoundInCycle0Scenario() throws JsonProcessingException {
        String verifiedPid = "some random string";
        testDatabaseRule.ensurePidExist(verifiedPid);
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
    public void shouldReturnNoMatchWhenUserWithSurnameAndDateOfBirthIsNotFoundInCycle1Scenario() throws JsonProcessingException {
        String verifiedPid = "some random string";
        testDatabaseRule.ensurePidDoesNotExist(verifiedPid);
        LocalDate dateOfBirth = LocalDate.of(1884, 4, 6);
        testDatabaseRule.ensureUserDoesNotExist("test surname", dateOfBirth);
        MatchingAttributesValueDto verifiedSurname = aMatchingAttributesValueDtoBuilder()
                .withVerified(true)
                .withValue("test surname")
                .build();
        MatchingAttributesValueDto verifiedDateOfBirth = aMatchingAttributesValueDtoBuilder()
                .withVerified(true)
                .withValue(dateOfBirth)
                .build();
        MatchingServiceRequestDto matchingServiceRequestDto = aMatchingServiceRequestDtoBuilder()
                .withHashedPid(verifiedPid)
                .withMatchingAttributesDto(aMatchingAttributesDtoBuilder()
                        .withDateOfBirth(verifiedDateOfBirth)
                        .withSurname(verifiedSurname).build())
                .build();

        Response response = APP_RULE.client()
                .target(String.format("http://localhost:%d/match-user", APP_RULE.getLocalPort()))
                .request()
                .post(Entity.entity(getRequestString(matchingServiceRequestDto), MediaType.APPLICATION_JSON_TYPE));

        assertEquals(200, response.getStatus());
        assertEquals(MatchStatusResponseDto.NO_MATCH, response.readEntity(MatchStatusResponseDto.class));
    }

    @Test
    public void shouldReturnMatchWhenUserWithSurnameAndDateOfBirthIsFoundInCycle1Scenario() throws JsonProcessingException {
        MatchingAttributesValueDto verifiedSurname = aMatchingAttributesValueDtoBuilder()
                .withVerified(true)
                .withValue("test surname")
                .build();
        LocalDate dateOfBirth = LocalDate.of(1884, 4, 6);
        MatchingAttributesValueDto verifiedDateOfBirth = aMatchingAttributesValueDtoBuilder()
                .withVerified(true)
                .withValue(dateOfBirth)
                .build();
        MatchingServiceRequestDto matchingServiceRequestDto = aMatchingServiceRequestDtoBuilder()
                .withHashedPid("some random string")
                .withMatchingAttributesDto(aMatchingAttributesDtoBuilder()
                        .withDateOfBirth(verifiedDateOfBirth)
                        .withSurname(verifiedSurname).build())
                .build();
        testDatabaseRule.ensurePidDoesNotExist("some random string");
        testDatabaseRule.ensureUserExist("test surname", dateOfBirth);

        Response response = APP_RULE.client()
                .target(String.format("http://localhost:%d/match-user", APP_RULE.getLocalPort()))
                .request()
                .post(Entity.entity(getRequestString(matchingServiceRequestDto), MediaType.APPLICATION_JSON_TYPE));

        assertEquals(200, response.getStatus());
        assertEquals(MatchStatusResponseDto.MATCH, response.readEntity(MatchStatusResponseDto.class));
    }

    @Test
    public void shouldReturnMatchWhenThereIsCaseInsensitiveSurnameMatchFoundInCycle1Scenario() throws JsonProcessingException {
        MatchingAttributesValueDto verifiedSurname = aMatchingAttributesValueDtoBuilder()
                .withVerified(true)
                .withValue("Test surname")
                .build();
        LocalDate dateOfBirth = LocalDate.of(1884, 4, 6);
        MatchingAttributesValueDto verifiedDateOfBirth = aMatchingAttributesValueDtoBuilder()
                .withVerified(true)
                .withValue(dateOfBirth)
                .build();
        MatchingServiceRequestDto matchingServiceRequestDto = aMatchingServiceRequestDtoBuilder()
                .withHashedPid("some random string")
                .withMatchingAttributesDto(aMatchingAttributesDtoBuilder()
                        .withDateOfBirth(verifiedDateOfBirth)
                        .withSurname(verifiedSurname).build())
                .build();
        testDatabaseRule.ensurePidDoesNotExist("some random string");
        testDatabaseRule.ensureUserExist("TeST Surname", dateOfBirth);

        Response response = APP_RULE.client()
                .target(String.format("http://localhost:%d/match-user", APP_RULE.getLocalPort()))
                .request()
                .post(Entity.entity(getRequestString(matchingServiceRequestDto), MediaType.APPLICATION_JSON_TYPE));

        assertEquals(200, response.getStatus());
        assertEquals(MatchStatusResponseDto.MATCH, response.readEntity(MatchStatusResponseDto.class));
    }

    private String getRequestString(MatchingServiceRequestDto matchingServiceRequestDto) throws JsonProcessingException {
        return APP_RULE.getObjectMapper().writeValueAsString(matchingServiceRequestDto);
    }
}
