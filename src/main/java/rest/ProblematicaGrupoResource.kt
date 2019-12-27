package rest

import entity.Grupo
import usecase.GrupoUseCase
import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Path("/problematicas")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class ProblematicaGrupoResource(val grupoUseCase: GrupoUseCase){

    @GET
    @Path("/{idProblematica}/grupos")
    fun darGruposConReaccionPropia(@PathParam("idProblematica") idProblematica: Int, @QueryParam("email") email: String) =
        grupoUseCase.darGruposConReaccionDeUsuario(idProblematica, email)


}