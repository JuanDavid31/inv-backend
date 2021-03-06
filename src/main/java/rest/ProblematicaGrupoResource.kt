package rest

import usecase.EscritoUseCase
import usecase.GrupoUseCase
import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Path("/problematicas")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class ProblematicaGrupoResource(val grupoUseCase: GrupoUseCase, val escritoUseCase: EscritoUseCase){

//    @GET
//    @Path("/{idProblematica}/grupos/{idGrupo}/personas/{email}/escritos")
//    fun darEscrito(@PathParam("idProblematica") idProblematica: Int,
//                   @PathParam("idGrupo") idGrupo: Int,
//                   @PathParam("email") email: String): Response{
//        val optionalEscrito = escritoUseCase.darEscrito(idProblematica, idGrupo, email)
//        return if(optionalEscrito.isPresent()) Response.ok(optionalEscrito.get()).build() else Response.ok().build()
//    }
}