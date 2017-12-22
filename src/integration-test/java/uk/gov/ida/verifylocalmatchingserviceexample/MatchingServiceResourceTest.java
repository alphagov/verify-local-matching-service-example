package uk.gov.ida.verifylocalmatchingserviceexample;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.dropwizard.testing.junit.DropwizardAppRule;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import uk.gov.ida.verifylocalmatchingserviceexample.configuration.VerifyLocalMatchingServiceExampleConfiguration;
import uk.gov.ida.verifylocalmatchingserviceexample.contracts.AddressDto;
import uk.gov.ida.verifylocalmatchingserviceexample.contracts.MatchStatusResponseDto;
import uk.gov.ida.verifylocalmatchingserviceexample.contracts.MatchingAttributesValueDto;
import uk.gov.ida.verifylocalmatchingserviceexample.contracts.MatchingServiceRequestDto;
import uk.gov.ida.verifylocalmatchingserviceexample.rules.TestDatabaseRule;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import java.time.LocalDate;
import java.util.Arrays;

import static io.dropwizard.testing.ResourceHelpers.resourceFilePath;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static uk.gov.ida.verifylocalmatchingserviceexample.builders.AddressDtoBuilder.anAddressDtoBuilder;
import static uk.gov.ida.verifylocalmatchingserviceexample.builders.MatchingAttributesDtoBuilder.aMatchingAttributesDtoBuilder;
import static uk.gov.ida.verifylocalmatchingserviceexample.builders.MatchingAttributesValueDtoBuilder.aMatchingAttributesValueDtoBuilder;
import static uk.gov.ida.verifylocalmatchingserviceexample.builders.MatchingServiceRequestDtoBuilder.aMatchingServiceRequestDtoBuilder;
import static uk.gov.ida.verifylocalmatchingserviceexample.contracts.MatchStatusResponseDto.MATCH;
import static uk.gov.ida.verifylocalmatchingserviceexample.contracts.MatchStatusResponseDto.NO_MATCH;

public class MatchingServiceResourceTest {

    private static final DropwizardAppRule<VerifyLocalMatchingServiceExampleConfiguration> APP_RULE = new DropwizardAppRule<>(
        VerifyLocalMatchingServiceExampleApplication.class,
        resourceFilePath("verify-local-matching-service-test.yml")
    );

    private static TestDatabaseRule testDatabaseRule = new TestDatabaseRule(APP_RULE);

    @ClassRule
    public static RuleChain chain = RuleChain.outerRule(APP_RULE).around(testDatabaseRule);

    @Before
    public void setUp() {
        testDatabaseRule.eraseAllData();
    }

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
            .post(Entity.entity(getRequestString(matchingServiceRequestDto), APPLICATION_JSON_TYPE));

        assertEquals(200, response.getStatus());
        assertEquals(MATCH, response.readEntity(MatchStatusResponseDto.class));
    }

    @Test
    public void shouldSavePidAndReturnMatchWhenOneUserWithCaseInsensitiveSurnameDateOfBirthAndCaseInsensitivePostcodeIsFoundInCycle1Scenario() throws JsonProcessingException {
        LocalDate dateOfBirth = LocalDate.of(1884, 4, 6);

        testDatabaseRule.ensureUserWithAddressExist("some-SURNAME", dateOfBirth, "some-postcode");

        MatchingAttributesValueDto verifiedSurname = aMatchingAttributesValueDtoBuilder()
            .withVerified(true)
            .withValue("SOME-surname")
            .build();
        MatchingAttributesValueDto verifiedDateOfBirth = aMatchingAttributesValueDtoBuilder()
            .withVerified(true)
            .withValue(dateOfBirth)
            .build();
        AddressDto address = anAddressDtoBuilder().withPostCode("SOME-postcode").withVerified(true).build();
        MatchingServiceRequestDto matchingServiceRequestDto = aMatchingServiceRequestDtoBuilder()
            .withHashedPid("some-pid")
            .withMatchingAttributesDto(aMatchingAttributesDtoBuilder()
                .withDateOfBirth(verifiedDateOfBirth)
                .withAddresses(Arrays.asList(address))
                .withSurname(verifiedSurname).build())
            .build();

        Response response = APP_RULE.client()
            .target(String.format("http://localhost:%d/match-user", APP_RULE.getLocalPort()))
            .request()
            .post(Entity.entity(getRequestString(matchingServiceRequestDto), APPLICATION_JSON_TYPE));

        assertEquals(200, response.getStatus());
        assertEquals(MATCH, response.readEntity(MatchStatusResponseDto.class));
        assertTrue(testDatabaseRule.checkIfPidExist(matchingServiceRequestDto.getHashedPid()));
    }

    @Test
    public void shouldReturnNoMatchWhenMultipleUserWithSurnameDateOfBirthAndPostcodeIsFoundInCycle1Scenario() throws JsonProcessingException {
        LocalDate dateOfBirth = LocalDate.of(1884, 4, 6);

        testDatabaseRule.ensurePidDoesNotExist("some-pid");
        testDatabaseRule.ensureUserWithAddressExist("some-surname", dateOfBirth, "some-postcode");
        testDatabaseRule.ensureUserWithAddressExist("some-surname", dateOfBirth, "some-postcode");

        MatchingAttributesValueDto verifiedSurname = aMatchingAttributesValueDtoBuilder()
            .withVerified(true)
            .withValue("some-surname")
            .build();
        MatchingAttributesValueDto verifiedDateOfBirth = aMatchingAttributesValueDtoBuilder()
            .withVerified(true)
            .withValue(dateOfBirth)
            .build();
        AddressDto address = anAddressDtoBuilder().withPostCode("SOME-postcode").withVerified(true).build();
        MatchingServiceRequestDto matchingServiceRequestDto = aMatchingServiceRequestDtoBuilder()
            .withHashedPid("some-pid")
            .withMatchingAttributesDto(aMatchingAttributesDtoBuilder()
                .withDateOfBirth(verifiedDateOfBirth)
                .withAddresses(Arrays.asList(address))
                .withSurname(verifiedSurname).build())
            .build();

        Response response = APP_RULE.client()
            .target(String.format("http://localhost:%d/match-user", APP_RULE.getLocalPort()))
            .request()
            .post(Entity.entity(getRequestString(matchingServiceRequestDto), APPLICATION_JSON_TYPE));

        assertEquals(200, response.getStatus());
        assertEquals(NO_MATCH, response.readEntity(MatchStatusResponseDto.class));
    }

    private String getRequestString(MatchingServiceRequestDto matchingServiceRequestDto) throws JsonProcessingException {
        return APP_RULE.getObjectMapper().writeValueAsString(matchingServiceRequestDto);
    }
}
