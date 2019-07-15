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

    @Path("/{idProblematica}/gruposActivos")
    @GET
    fun darGrupos(@PathParam("idProblematica") idProblematica: Int) =
            grupoUseCase.darGrupos(idProblematica)

    //TODO: Probablemente esto sea reemplazado por los Websockets

    @POST
    @Path("/{idProblematica}/gruposActivos")
    fun agregarGrupo(@PathParam("idProblematica") idProblematica: Int, grupo: Grupo) = grupoUseCase.agregarGrupo(idProblematica, grupo)

    @Path("/{idProblematica}/gruposActivos/{idGrupo}")
    @DELETE
    fun eliminarGrupo(@PathParam("idProblematica") idProblematica: Int,
                      @PathParam("idGrupo") idGrupo: Int): Response {
        val seElimino = grupoUseCase.eliminarGrupo(idGrupo, idProblematica)
        return if (seElimino) Response.ok().build() else Response.status(Response.Status.BAD_REQUEST).build()
    }
}