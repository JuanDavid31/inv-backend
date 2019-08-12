package rest

import entity.Error
import entity.Persona
import filter.ValidPersonaLogin
import org.hibernate.validator.constraints.NotEmpty
import org.jetbrains.annotations.NotNull
import usecase.CorreoUseCase
import usecase.PersonaUseCase
import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Path("/auth")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
class AuthResource(private val personaUseCase: PersonaUseCase, private val correoUseCase: CorreoUseCase) {

    @POST
    fun credencialesCorrectas(@NotNull @ValidPersonaLogin persona: Persona): Response {
        val resultado = personaUseCase.darPersonaPorCredenciales(persona)
        return when(resultado){
            is Error -> Response.status(Response.Status.BAD_REQUEST).entity(resultado).build()
            else -> Response.ok(resultado).build()
        }
    }

    @POST
    @Path("/pass")
    fun olvidePass(@QueryParam("email") @NotEmpty email: String): Response {
        val resultado = correoUseCase.enviarCorreo(email)
        return when(resultado){
            is Error -> Response.status(Response.Status.BAD_REQUEST).entity(resultado).build()
            else -> Response.ok(resultado).build()
        }
    }
}