package uk.gov.ida.verifylocalmatchingserviceexample.resources;

import uk.gov.ida.verifylocalmatchingserviceexample.contracts.MatchingServiceRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/match-user")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MatchingServiceResource {

    @POST
    public Response findMatchingUser(@NotNull @Valid MatchingServiceRequestDto matchingServiceRequest) {
        return Response.ok()
                .entity("{\"result\": \"match\"}")
                .build();
    }
}
