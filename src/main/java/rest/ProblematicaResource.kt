package rest

import annotation.filter.ServerEventBroadcaster
import usecase.InvitacionUseCase
import usecase.ProblematicaUseCase
import javax.validation.constraints.NotNull
import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Path("/problematicas")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
class ProblematicaResource(private val InvitacionUseCase: InvitacionUseCase, private val problematicaUseCase: ProblematicaUseCase) {

    @Path("/{idProblematica}/personas/{emailRemitente}/invitaciones")
    @GET
    fun darPersonasInvitadas(@PathParam("idProblematica") idProblematica: Int,
                             @PathParam("emailRemitente") emailRemitente: String): Response {
        val personas = InvitacionUseCase.darPersonasInvitadas(emailRemitente, idProblematica)
        return Response.ok(personas).build()
    }

    @Path("/{idProblematica}")
    @GET
    fun darEstadoProblematica(@PathParam("idProblematica") idProblematica: Int,
                              @QueryParam("estado") @NotNull avanzar: Boolean?): Response{
        val resultado = problematicaUseCase.darEstadoProblematica(idProblematica)
        return when(resultado){
            is Error -> Response.status(Response.Status.BAD_REQUEST).entity(resultado).build()
            else -> Response.ok(resultado).build()
        }
    }

    @Path("/{idProblematica}")
    @PUT
    @ServerEventBroadcaster
    fun avanzarFase(@PathParam("idProblematica") idProblematica: Int,
                    @QueryParam("avanzar") @NotNull avanzar: Boolean?): Response {

        val resultado = problematicaUseCase.avanzarFase(idProblematica)
        return when(resultado){
            is Error -> Response.status(Response.Status.BAD_REQUEST).entity(resultado).build()
            else -> Response.ok(resultado).build()
        }
    }
}