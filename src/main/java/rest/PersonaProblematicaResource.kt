package rest

import annotation.filter.VerificadorAuth
import dao.DaoNodo
import entity.Error
import entity.Problematica
import entity.Reaccion
import usecase.GrupoUseCase
import usecase.ProblematicaUseCase
import usecase.ReaccionUseCase
import javax.validation.Valid
import javax.validation.constraints.NotNull
import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Path("/personas")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class PersonaProblematicaResource(private val problematicaUseCase: ProblematicaUseCase,
                                  private val daoNodo: DaoNodo,
                                  private val grupoUseCase: GrupoUseCase,
                                  private val reaccionUseCase: ReaccionUseCase) {

    @GET
    @Path("/{email}/problematicas/{idProblematica}/grupos")
    fun darGruposConReaccionPropia(@PathParam("email") email: String,
                                   @PathParam("idProblematica") idProblematica: Int)
            = grupoUseCase.darGruposConReaccionDeUsuario(idProblematica, email)

    @GET
    @Path("/{email}/problematicas")
//    @VerificadorAuth
    fun darProblematicasPorPersona(@PathParam("email") email: String,
                                   @QueryParam("fase") fase: Int?)=
        when(fase){
            5 ->  problematicaUseCase.darProblematicasTerminadasPorPersona(email)
            else -> problematicaUseCase.darProblematicasPorPersona(email)
        }


    @GET
    @Path("/{email}/problematicas/{idProblematica}/nodos")
    fun darNodos(@PathParam("email") email: String,
                 @PathParam("idProblematica") idProblematica: Int): Response {
        val nodos = daoNodo.darNodosPorPersonaYProblematica(email + idProblematica)
        return Response.ok(nodos).build()
    }

    @POST
    @Path("/{email}/problematicas")
    @VerificadorAuth
    fun agregarProblematicaPorPersona(@PathParam("email") email: String,
                                      @Valid @NotNull problematica: Problematica): Response {
        val resultado = problematicaUseCase.agregarProblematicaPorPersona(email, problematica)
        return when(resultado){
            is Error -> Response.status(Response.Status.BAD_REQUEST).entity(resultado).build()
            else -> Response.ok(resultado).build()
        }
    }

    @POST
    @Path("/{email}/problematicas/{idProblematica}/grupos/{idGrupo}/reacciones")
    fun reaccionar(@PathParam("email") email: String,
                   @PathParam("idProblematica") idProblematica: Int,
                   @PathParam("idGrupo") idGrupo: Int,
                   reaccion: Reaccion)
            = reaccionUseCase.reaccionar(reaccion, idGrupo, "$email$idProblematica")
}