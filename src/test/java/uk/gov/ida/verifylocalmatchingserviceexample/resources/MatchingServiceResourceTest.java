package uk.gov.ida.verifylocalmatchingserviceexample.resources;

import io.dropwizard.jersey.jackson.JsonProcessingExceptionMapper;
import io.dropwizard.jersey.validation.JerseyViolationExceptionMapper;
import io.dropwizard.testing.junit.ResourceTestRule;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.ClassRule;
import org.junit.Test;
import uk.gov.ida.verifylocalmatchingserviceexample.contracts.*;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class MatchingServiceResourceTest {
    @ClassRule
    public static final ResourceTestRule resources = ResourceTestRule.builder()
            .addProvider(JerseyViolationExceptionMapper.class)
            .addProvider(JsonProcessingExceptionMapper.class)
            .addResource(new MatchingServiceResource())
            .build();

    @Test
    public void shouldReturnOKResponse() {
        List<MatchingAttributesValueDto<String>> surnames = new ArrayList<>();
        MatchingAttributesValueDto<String> surname = new MatchingAttributesValueDto<String>("surname1", null, null, false);
        surnames.add(surname);
        ArrayList<AddressDto> addressDtos = new ArrayList<>();
        AddressDto addressDto = new AddressDto(false, DateTime.now(), null, null, null, null, null);
        addressDtos.add(addressDto);
        MatchingAttributesValueDto<LocalDate> dob = new MatchingAttributesValueDto<LocalDate>(LocalDate.now(), null, null, false);
        MatchingAttributesDto matchingAttributesDto = new MatchingAttributesDto(null, null, surnames, null, dob, addressDtos);
        MatchingServiceRequestDto matchingServiceRequest =
                new MatchingServiceRequestDto(matchingAttributesDto, null, "esds", "asdsd", LevelOfAssuranceDto.LEVEL_1);

        Response response = resources.target("/match-user")
                .request()
                .post(Entity.entity(matchingServiceRequest, MediaType.APPLICATION_JSON_TYPE));

        assertEquals(response.getStatus(), Response.Status.OK.getStatusCode());
        HashMap<String, String> matchResponse = response.readEntity(new GenericType<HashMap<String, String>>() {{ }});
        assertEquals(matchResponse.keySet(), new HashSet<String>() {{ add("result"); }});
        assertEquals(matchResponse.get("result"), "match");
    }
}