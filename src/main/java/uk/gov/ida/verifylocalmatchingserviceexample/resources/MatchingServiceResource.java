package uk.gov.ida.verifylocalmatchingserviceexample.resources;

import uk.gov.ida.verifylocalmatchingserviceexample.contracts.MatchStatusResponseDto;
import uk.gov.ida.verifylocalmatchingserviceexample.contracts.MatchingServiceRequestDto;
import uk.gov.ida.verifylocalmatchingserviceexample.service.Cycle0MatchingService;

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
    private Cycle0MatchingService cycle0MatchingService;

    public MatchingServiceResource(Cycle0MatchingService cycle0MatchingService) {
        this.cycle0MatchingService = cycle0MatchingService;
    }

    @POST
    public Response findMatchingUser(@NotNull @Valid MatchingServiceRequestDto matchingServiceRequest) {
        MatchStatusResponseDto matchStatusResponse = cycle0MatchingService.checkForPid(matchingServiceRequest.getHashedPid());
        return Response.ok(matchStatusResponse)
                .build();
    }
}
