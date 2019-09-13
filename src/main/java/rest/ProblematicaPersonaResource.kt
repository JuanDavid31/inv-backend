package rest

import entity.Escrito
import entity.Nodo
import org.glassfish.jersey.media.multipart.FormDataParam
import usecase.EscritoUseCase
import usecase.FotoUseCase
import usecase.PersonaUseCase
import java.io.InputStream
import javax.validation.constraints.Min
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size
import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Path("/problematicas")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class ProblematicaPersonaResource(val escritoUseCase: EscritoUseCase, val fotoUseCase: FotoUseCase, val personaUseCase: PersonaUseCase){

    @Path("/{idProblematica}/personas/{email}/nodos")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    @POST
    fun subirNodo(@NotNull @HeaderParam("extension") extensionFoto: String,
                  @NotNull @FormDataParam("foto") foto: InputStream,
                  @NotNull @FormDataParam("nombre") nombre: String,
                  @PathParam("email") email: String,
                  @PathParam("idProblematica") idProblematica: Int): Response {
        val nodo = fotoUseCase.guardarFoto(Nodo(nombre, email, idProblematica), foto, extensionFoto)
        return if (nodo != null) Response.ok(nodo).build() else Response.status(Response.Status.BAD_REQUEST).build()
    }

    @GET
    @Path("/{idProblematica}/personas/{email}/escritos")
    fun darEscritosPorPersona(@PathParam("idProblematica") idProblematica: Int,
                              @PathParam("email") email: String): Response{
        val optionalEscrito = escritoUseCase.darEscritoPorPersona("$email$idProblematica")
        return if(optionalEscrito.isPresent)return Response.ok(optionalEscrito.get()).build() else Response.ok().build()
    }

    @GET
    @Path("/{idProblematica}/personas")
    fun darPersonasPorCorreoNoInvitadas(@NotNull(message = "no puede ser nulo")
                                        @Min(value = 1, message = "debe ser mayor a 1")
                                        @PathParam("idProblematica") idProblematica: Int,
                                        @Size(min = 5, message = "debe tener al menos 5 caracteres")
                                        @QueryParam("email") email: String,
                                        @NotNull(message = "no puede ser nulo")
                                        @QueryParam("email-remitente")
                                        emailRemitente: String): Response{
        val personas = personaUseCase.darPersonasPorCorreoNoInvitadas(email, emailRemitente, idProblematica)
        return Response.ok(personas).build()
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