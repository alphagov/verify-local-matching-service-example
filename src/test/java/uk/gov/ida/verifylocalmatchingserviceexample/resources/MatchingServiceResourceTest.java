package uk.gov.ida.verifylocalmatchingserviceexample.resources;

import io.dropwizard.jersey.errors.ErrorMessage;
import io.dropwizard.jersey.jackson.JsonProcessingExceptionMapper;
import io.dropwizard.jersey.validation.JerseyViolationExceptionMapper;
import io.dropwizard.jersey.validation.ValidationErrorMessage;
import io.dropwizard.testing.junit.ResourceTestRule;
import org.junit.ClassRule;
import org.junit.Test;
import uk.gov.ida.verifylocalmatchingserviceexample.contracts.MatchStatusResponseDto;
import uk.gov.ida.verifylocalmatchingserviceexample.contracts.MatchingServiceRequestDto;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.HashSet;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static uk.gov.ida.verifylocalmatchingserviceexample.builders.MatchingServiceRequestDtoBuilder.aMatchingServiceRequestDtoBuilder;

public class MatchingServiceResourceTest {
    @ClassRule
    public static final ResourceTestRule resources = ResourceTestRule.builder()
            .addProvider(JerseyViolationExceptionMapper.class)
            .addProvider(JsonProcessingExceptionMapper.class)
            .addResource(new MatchingServiceResource())
            .build();

    @Test
    public void shouldReturnOKWhenThereIsNoConstraintViolation() {
        MatchingServiceRequestDto matchingServiceRequest = aMatchingServiceRequestDtoBuilder().build();

        Response response = postToMatchingService(matchingServiceRequest);

        assertEquals(response.getStatus(), Response.Status.OK.getStatusCode());
    }

    @Test
    public void shouldContainMatchForSuccessResponse() {
        MatchingServiceRequestDto matchingServiceRequest = aMatchingServiceRequestDtoBuilder().build();

        Response response = postToMatchingService(matchingServiceRequest);

        MatchStatusResponseDto matchStatusResponseDto = response.readEntity(MatchStatusResponseDto.class);
        assertEquals(matchStatusResponseDto.getResult(), MatchStatusResponseDto.MATCH.getResult());
    }

    @Test
    public void shouldReturn422WhenThereIsRequestValidationError() {
        MatchingServiceRequestDto matchingServiceRequest = aMatchingServiceRequestDtoBuilder()
                .withMatchingAttributesDto(null)
                .build();

        Response response = postToMatchingService(matchingServiceRequest);

        assertThat(response.getStatus()).isEqualTo(422);

        ValidationErrorMessage errorMessage = response.readEntity(ValidationErrorMessage.class);
        assertThat(errorMessage.getErrors().size()).isEqualTo(1);
        assertThat(errorMessage.getErrors().get(0)).isEqualTo("matchingAttributesDto may not be null");
    }

    @Test
    public void shouldReturn422ForBadJson() {
        Response response = resources.target("/match-user")
                .request()
                .post(Entity.entity("{}", MediaType.APPLICATION_JSON_TYPE));

        assertThat(response.getStatus()).isEqualTo(422);

        ValidationErrorMessage errorMessage = response.readEntity(ValidationErrorMessage.class);
        assertFalse(errorMessage.getErrors().isEmpty());
    }

    @Test
    public void shouldReturn400ForMalformedJson() {
        Response response = resources.target("/match-user")
                .request()
                .post(Entity.entity("not valid json", MediaType.APPLICATION_JSON_TYPE));

        assertThat(response.getStatus()).isEqualTo(BAD_REQUEST.getStatusCode());

        ErrorMessage actualError = response.readEntity(ErrorMessage.class);
        assertThat(actualError.getCode()).isEqualTo(BAD_REQUEST.getStatusCode());
        assertThat(actualError.getMessage()).isEqualTo("Unable to process JSON");
    }

    private Response postToMatchingService(MatchingServiceRequestDto matchingServiceRequest) {
        return resources.target("/match-user")
                .request()
                .post(Entity.entity(matchingServiceRequest, MediaType.APPLICATION_JSON_TYPE));
    }
}