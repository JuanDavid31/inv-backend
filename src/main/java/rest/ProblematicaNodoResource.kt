package rest

import entity.Nodo
import org.glassfish.jersey.media.multipart.FormDataParam
import usecase.FotoUseCase
import java.io.InputStream
import javax.validation.constraints.NotNull
import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Path("/problematicas")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class ProblematicaNodoResource(val fotoUseCase: FotoUseCase){

    @Path("/{idProblematica}/nodos")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @POST
    fun subirNodo(@NotNull @HeaderParam("extension") extensionFoto: String,
                  @NotNull @FormDataParam("foto") foto: InputStream,
                  @NotNull @FormDataParam("email") email: String,
                  @PathParam("idProblematica") idProblematica: Int): Response {
        val nodo = fotoUseCase.guardarFoto(Nodo(email, idProblematica), foto, extensionFoto)
        return if (nodo != null) Response.ok(nodo).build() else Response.status(Response.Status.BAD_REQUEST).build()
    }
}