package rest

import dao.DaoEscrito
import entity.Escrito
import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Path("/problematicas")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class ProblematicaEscritoResource(val daoEscrito: DaoEscrito){

    @GET
    @Path("/{idProblematica}/escritos")
    fun darEscritosPorProblematica(@PathParam("idproblematica") idProblematica: Int) = daoEscrito.darEscritosPorProblematica(idProblematica)

    @GET
    @Path("/{idProblematica}/personas/{email}/escritos")
    fun darEscritosPorPersona(@PathParam("idProblematica") idProblematica: Int,
                              @PathParam("email") email: String) =
            daoEscrito.darEscritoPorPersona("$email$idProblematica")

    @POST
    @Path("/{idProblematica}/personas/{email}/escritos")
    fun agregarEscrito(@PathParam("idProblematica") idProblematica: Int,
                       @PathParam("email") email: String,
                       escrito: Escrito) =
            daoEscrito.agregarEscrito(escrito, "$email$idProblematica")

    @PUT
    @Path("/{idProblematica}/personas/{email}/escritos/{idEscrito}")
    fun editarEscrito(@PathParam("idProblematica") idProblematica: Int,
                      @PathParam("email") email: String,
                      @PathParam("idEscrito") idEscrito: String,
                      escrito: Escrito): Response{
        val seEdito = daoEscrito.editarEscrito(escrito, "$email$idProblematica", idEscrito)
        return if (seEdito) Response.ok().build() else Response.status(Response.Status.BAD_REQUEST).build()
    }
}