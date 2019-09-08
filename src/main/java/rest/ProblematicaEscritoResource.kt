package rest

import usecase.EscritoUseCase
import javax.ws.rs.*
import javax.ws.rs.core.MediaType

@Path("/problematicas")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class ProblematicaEscritoResource(val escritoUseCase: EscritoUseCase){

    @GET
    @Path("/{idProblematica}/escritos")
    fun darEscritosPorProblematica(@PathParam("idProblematica") idProblematica: Int) = escritoUseCase.darEscritosPorProblematica(idProblematica)
}