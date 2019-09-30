package rest

import org.hibernate.validator.constraints.NotEmpty
import usecase.ReaccionUseCase

import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Path("/gruposActivos") //TODO: Por qué grupos activos?
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class GrupoResource(private val daoReaccion: ReaccionUseCase) {

    @POST
    @Path("/{idGrupo}/reacciones")
    fun reaccionar(@PathParam("idGrupo") idGrupo: Int,
                   @QueryParam("valor") @NotEmpty valor: Int,
                   @QueryParam("id-persona-problematica") @NotEmpty idPersonaProblematica: String): Response {
        val seReacciono = daoReaccion.reaccionar(valor, idGrupo, idPersonaProblematica)
        return if (seReacciono) Response.ok().build() else Response.status(Response.Status.BAD_REQUEST).build()
    }
}