package rest

import dao.DaoGrupo
import entity.Grupo
import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Path("/problematicas")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class ProblematicaGrupoResource(val daoGrupo: DaoGrupo){

    @Path("/{idProblematica}/grupos")
    @GET
    fun darGrupos(@PathParam("idProblematica") idProblematica: Int) =
            daoGrupo.darGrupos(idProblematica)

    //TODO: Probablemente esto sea reemplazado por los Websockets

    @POST
    @Path("/{idProblematica}/grupos")
    fun agregarGrupo(@PathParam("idProblematica") idProblematica: Int, grupo: Grupo) = daoGrupo.agregarGrupo(idProblematica, grupo)

    @Path("/{idProblematica}/grupos/{idGrupo}")
    @DELETE
    fun eliminarGrupo(@PathParam("idProblematica") idProblematica: Int,
                      @PathParam("idGrupo") idGrupo: Int): Response {
        val seElimino = daoGrupo.eliminarGrupo(idGrupo, idProblematica)
        return if (seElimino) Response.ok().build() else Response.status(Response.Status.BAD_REQUEST).build()
    }
}