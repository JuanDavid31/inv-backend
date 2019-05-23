package rest

import usecase.InvitacionUseCase
import java.util.concurrent.CompletableFuture
import javax.ws.rs.*
import javax.ws.rs.core.MediaType

@Path("/personas")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class PersonaInvitacionResource(val invitacionUseCase: InvitacionUseCase){

    @GET
    @Path("/{email}/invitaciones")
    fun darInvitacionesVigentes(@PathParam("email") email: String) =
            invitacionUseCase.darInvitacionesVigentes(email)

    //TODO: Pruebas con hilos
    @GET
    @Path("/hilo1")
    fun hilo(): String {
        Thread.sleep(1000*5)
        return "Exito"
    }

    @GET
    @Path("/hilo2")
    fun hilo2(): String{
        val supplyAsync: CompletableFuture<String> = CompletableFuture.supplyAsync<String> {
            Thread.sleep(1000 * 5)
            println("Exito")
            "Exito"
        }

        return supplyAsync.get()
    }

    @GET
    @Path("/hilo3")
    fun hilo3() = "Exito"
}