package rest

import dao.DaoEscrito
import javax.ws.rs.*
import javax.ws.rs.core.MediaType

@Path("/problematicas")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class ProblematicaEscritoResource(val daoEscrito: DaoEscrito){

    @GET
    @Path("/{idProblematica}/escritos")
    fun darEscritosPorProblematica(@PathParam("idproblematica") idProblematica: Int) = daoEscrito.darEscritosPorProblematica(idProblematica)
}