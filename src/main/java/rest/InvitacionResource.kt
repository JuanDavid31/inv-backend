package rest

import annotation.filter.ServerEventBroadcaster
import entity.Error
import entity.Invitacion
import org.hibernate.validator.constraints.NotEmpty
import usecase.InvitacionUseCase
import javax.validation.Valid
import javax.validation.constraints.NotNull
import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Path("/invitaciones")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class InvitacionResource(private val invitacionUseCase: InvitacionUseCase) {

    @POST
    @ServerEventBroadcaster
    fun hacerInvitacion(@Valid @NotNull invitacion: Invitacion): Response {
        val resultado = invitacionUseCase.hacerInvitacion(invitacion)
        return when(resultado){
            is Error -> Response.status(Response.Status.BAD_REQUEST).entity(resultado).build()
            else -> Response.ok(resultado).build()
        }
    }

    @PUT
    @Path("/{idInvitacion}")
    @ServerEventBroadcaster
    fun responderInvitacion(@PathParam("idInvitacion") idInvitacion: String,
                            @Valid @NotNull invitacion: Invitacion,
                            @QueryParam("aceptar") @NotNull aceptar: Boolean): Response {
        val resultado: Any
        if (aceptar) {
            resultado = invitacionUseCase.aceptarInvitacion(invitacion, idInvitacion)
        } else {
            resultado = invitacionUseCase.rechazarInvitacion(invitacion, idInvitacion)
        }
        return when(resultado){
            is Error -> Response.status(Response.Status.BAD_REQUEST).entity(resultado).build()
            else -> Response.ok(resultado).build()
        }
    }

    @DELETE
    @Path("/{idInvitacion}")
    fun eliminarInvitacion(@PathParam("idInvitacion") idInvitacion: String): Response {
        val resultado = invitacionUseCase.eliminarInvitacion(idInvitacion)
        return when(resultado){
            is Error -> Response.status(Response.Status.BAD_REQUEST).entity(resultado).build()
            else -> Response.ok(resultado).build()
        }
    }
}