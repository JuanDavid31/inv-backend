package rest

import dao.DaoInvitacion
import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Path("/personas")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class PersonaInvitacionResource(val daoInvitacion: DaoInvitacion){

    @GET
    @Path("/{email}/invitaciones")
    fun darInvitacionesVigentes(@PathParam("email") email: String) =
            daoInvitacion.darInvitacionesVigentes(email)
}