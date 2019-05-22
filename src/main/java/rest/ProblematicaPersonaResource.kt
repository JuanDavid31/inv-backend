package rest

import entity.Escrito
import entity.Nodo
import org.glassfish.jersey.media.multipart.FormDataParam
import usecase.EscritoUseCase
import usecase.FotoUseCase
import java.io.InputStream
import javax.validation.constraints.NotNull
import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Path("/problematicas")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class ProblematicaPersonaResource(val escritoUseCase: EscritoUseCase, val fotoUseCase: FotoUseCase){

    @Path("/{idProblematica}/personas/{email}/nodos")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @POST
    fun subirNodo(@NotNull @HeaderParam("extension") extensionFoto: String,
                  @NotNull @FormDataParam("foto") foto: InputStream,
                  @PathParam("email") email: String,
                  @PathParam("idProblematica") idProblematica: Int): Response {
        val nodo = fotoUseCase.guardarFoto(Nodo(email, idProblematica), foto, extensionFoto)
        return if (nodo != null) Response.ok(nodo).build() else Response.status(Response.Status.BAD_REQUEST).build()
    }

    @GET
    @Path("/{idProblematica}/personas/{email}/escritos")
    fun darEscritosPorPersona(@PathParam("idProblematica") idProblematica: Int,
                              @PathParam("email") email: String): Response{
        val optionalEscrito = escritoUseCase.darEscritoPorPersona("$email$idProblematica")
        return if(optionalEscrito.isPresent)return Response.ok(optionalEscrito.get()).build() else Response.ok().build()
    }


    @POST
    @Path("/{idProblematica}/personas/{email}/escritos")
    fun agregarEscrito(@PathParam("idProblematica") idProblematica: Int,
                       @PathParam("email") email: String,
                       escrito: Escrito) =
            escritoUseCase.agregarEscrito(escrito, "$email$idProblematica")

    @PUT
    @Path("/{idProblematica}/personas/{email}/escritos/{idEscrito}")
    fun editarEscrito(@PathParam("idProblematica") idProblematica: Int,
                      @PathParam("email") email: String,
                      @PathParam("idEscrito") idEscrito: String,
                      escrito: Escrito): Response{
        val seEdito = escritoUseCase.editarEscrito(escrito, "$email$idProblematica", idEscrito)
        return if (seEdito) Response.ok().build() else Response.status(Response.Status.BAD_REQUEST).build()
    }
}