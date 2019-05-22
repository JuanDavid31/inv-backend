package rest

import usecase.InvitacionUseCase
import javax.ws.rs.*
import javax.ws.rs.core.MediaType

@Path("/personas")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class PersonaInvitacionResource(val invitacionUseCase: InvitacionUseCase){

    @GET
    @Path("/{email}/invitaciones")
    fun darInvitacionesVigentes(@PathParam("email") email: String) =
            invitacionUseCase.darInvitacionesVigentes(email)
}