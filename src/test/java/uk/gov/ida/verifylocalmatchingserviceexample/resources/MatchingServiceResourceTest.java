package uk.gov.ida.verifylocalmatchingserviceexample.resources;

import io.dropwizard.jersey.jackson.JsonProcessingExceptionMapper;
import io.dropwizard.jersey.validation.JerseyViolationExceptionMapper;
import io.dropwizard.testing.junit.ResourceTestRule;
import org.junit.ClassRule;
import org.junit.Test;
import uk.gov.ida.verifylocalmatchingserviceexample.contracts.MatchingServiceRequestDto;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.HashSet;

import static org.junit.Assert.assertEquals;
import static uk.gov.ida.verifylocalmatchingserviceexample.builders.MatchingServiceRequestDtoBuilder.aMatchingServiceRequestDtoBuilder;

public class MatchingServiceResourceTest {
    @ClassRule
    public static final ResourceTestRule resources = ResourceTestRule.builder()
            .addProvider(JerseyViolationExceptionMapper.class)
            .addProvider(JsonProcessingExceptionMapper.class)
            .addResource(new MatchingServiceResource())
            .build();

    @Test
    public void shouldReturnOKResponse() {
        MatchingServiceRequestDto matchingServiceRequest = aMatchingServiceRequestDtoBuilder().build();

        Response response = resources.target("/match-user")
                .request()
                .post(Entity.entity(matchingServiceRequest, MediaType.APPLICATION_JSON_TYPE));

        assertEquals(response.getStatus(), Response.Status.OK.getStatusCode());
        HashMap<String, String> matchResponse = response.readEntity(new GenericType<HashMap<String, String>>() {{ }});
        assertEquals(matchResponse.keySet(), new HashSet<String>() {{ add("result"); }});
        assertEquals(matchResponse.get("result"), "match");
    }
}