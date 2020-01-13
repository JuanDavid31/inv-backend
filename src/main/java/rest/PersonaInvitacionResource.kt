package rest

import usecase.InvitacionUseCase
import javax.ws.rs.*
import javax.ws.rs.core.MediaType

@Path("/personas")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class PersonaInvitacionResource(val invitacionUseCase: InvitacionUseCase){

    /**
     * Devuelve las invitaciones recibidas a otros usuarios que aun no han sido rechazadas
     */
    @GET
    @Path("/{email}/invitaciones")
    fun darInvitacionesVigentesRecibidas(@PathParam("email") email: String) =
            invitacionUseCase.darInvitacionesVigentes(email)
}