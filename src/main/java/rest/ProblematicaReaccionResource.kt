package rest

import usecase.GrupoUseCase
import javax.ws.rs.*
import javax.ws.rs.core.MediaType

@Path("/problematicas")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class ProblematicaReaccionResource(val GrupoUseCase: GrupoUseCase){

    @GET
    @Path("/{idProblematica}/reacciones")
    fun darGruposConReacciones(@PathParam("idProblematica") idProblematica: Int) =
            GrupoUseCase.darGruposConReacciones(idProblematica)

    @GET
    @Path("/{idProblematica}/personas/{email}/reacciones}")
    fun darReaccionPorPersona(@PathParam("idProblematica") idProblematica: Int,
                              @PathParam("idProblematica") email: String) =
            GrupoUseCase.darGrupoConReaccion(idProblematica, "$email$idProblematica")

}