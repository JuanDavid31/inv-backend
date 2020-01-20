package rest

import entity.Persona
import entity.Error
import usecase.PersonaUseCase

import javax.validation.Valid
import javax.validation.constraints.NotNull
import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Path("/personas")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class PersonaResource(private val personaUseCase: PersonaUseCase) {

    @POST
    fun agregarPersona(@Valid @NotNull persona: Persona): Response {
        val resultado = personaUseCase.agregarPersona(persona)
        return when(resultado){
            is Error -> Response.status(Response.Status.BAD_REQUEST).entity(resultado).build()
            else -> Response.ok(resultado).build()
        }
    }
}