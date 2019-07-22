package rest

import dao.DaoNodo
import entity.Persona
import entity.Problematica
import entity.Error
import filter.VerificadorAuth
import usecase.PersonaUseCase
import usecase.ProblematicaUseCase

import javax.validation.Valid
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size
import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Path("/personas")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class PersonaResource(private val problematicaUseCase: ProblematicaUseCase, private val personaUseCase: PersonaUseCase, private val daoNodo: DaoNodo) {

    @GET
    @Path("/{email}")
    fun darPersonasPorEmail(@PathParam("email")
                            @Size(min = 5, message = "debe tener al menos 5 caracteres")
                            email: String): Response{
        val personas = personaUseCase.darPersonasPorCorreo(email)
        return Response.ok(personas).build()
    }

    @POST
    fun agregarPersona(@Valid @NotNull persona: Persona): Response {
        val resultado = personaUseCase.agregarPersona(persona)
        return when(resultado){
            is Error -> Response.status(Response.Status.BAD_REQUEST).entity(resultado).build()
            else -> Response.ok(resultado).build()
        }
    }

    @GET
    @Path("/{email}/problematicas")
    @VerificadorAuth
    fun darProblematicasPorPersona(@PathParam("email") email: String): Response {
        val problematicas = problematicaUseCase.darProblematicasPorPersona(email)
        return Response.ok(problematicas).build()
    }

    @POST
    @Path("/{email}/problematicas")
    @VerificadorAuth
    fun agregarProblematicaPorPersona(@PathParam("email") email: String,
                                      @Valid @NotNull problematica: Problematica): Response {
        val nuevaProblematica: Problematica
            try {
            nuevaProblematica = problematicaUseCase.agregarProblematicaPorPersona(email, problematica)
        } catch (e: Exception) {
            e.printStackTrace()
            return Response.status(Response.Status.BAD_REQUEST).build()
        }
        return Response.ok(nuevaProblematica).build()
    }

    @GET
    @Path("/{email}/problematicas/{idProblematica}/nodos")
    fun darNodos(@PathParam("email") email: String,
                 @PathParam("idProblematica") idProblematica: Int?): Response {
        val nodos = daoNodo.darNodos(email + idProblematica!!)
        return Response.ok(nodos).build()
    }
}