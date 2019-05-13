package rest

import dao.DaoGrupo
import javax.ws.rs.*
import javax.ws.rs.core.MediaType

@Path("/problematicas")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class ProblematicaReaccionResource(val daoGrupo: DaoGrupo){

    @GET
    @Path("/{idProblematica}/reacciones")
    fun darReacciones(@PathParam("idProblematica") idProblematica: Int) = daoGrupo.darGruposConReacciones(idProblematica)

    @GET
    @Path("/{idProblematica}/reacciones/{idPersonaProblematica}")
    fun darReaccionPorPersona(@PathParam("idProblematica") idProblematica: Int,
                              @PathParam("idProblematica") idPersonaProblematica: String) =
            daoGrupo.darGrupoConReaccion(idProblematica, idPersonaProblematica)

}