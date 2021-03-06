package rest

import org.hibernate.validator.constraints.NotEmpty
import usecase.ReaccionUseCase
import javax.validation.constraints.NotNull

import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

//TODO: Usar filtro para auth
@Path("/grupos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class GrupoResource(private val reaccionUseCase: ReaccionUseCase) {

    @DELETE
    @Path("/{idGrupo}/reacciones/{id}")
    fun eliminarReaccion(@PathParam("idGrupo") idGrupo: Int,
                        @PathParam("id") idReaccion: Int): Response {
        val seElimino = reaccionUseCase.eliminarReaccion(idGrupo, idReaccion)
        return if (seElimino) Response.ok().build() else Response.status(Response.Status.BAD_REQUEST).build()
    }
}