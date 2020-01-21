package rest

import entity.Error
import entity.Escrito
import entity.Nodo
import org.glassfish.jersey.media.multipart.FormDataParam
import org.hibernate.validator.constraints.NotEmpty
import usecase.EscritoUseCase
import usecase.FotoUseCase
import usecase.PersonaUseCase
import java.io.InputStream
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
                              @PathParam("email") email: String)
            = escritoUseCase.darEscritosPorPersona("$email$idProblematica")

    /**
     * Busca personas no invitadas a una problematica dado un patron de email.
     * @param email Patron o fragmento de email por el cual se hara la busqueda
     * @param emailRemitente Email de la persona que esta realizando la busqueda.
     * Si esta no se incluyera entonces apareceria como resultado en la busqueda.
     * @param idProblematica Problematica a la cual se quiere invitar a las personas.
     */
    @GET
    @Path("/{idProblematica}/personas")
    fun darPersonasPorCorreoNoInvitadas(@PathParam("idProblematica") idProblematica: Int,
                                        @NotEmpty(message = "no puede estar vacio")
                                        @Size(min = 5, message = "debe tener al menos 5 caracteres")
                                        @QueryParam("email") email: String,
                                        @NotEmpty(message = "no puede estar vacio")
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
        val resultado = escritoUseCase.editarEscrito(escrito, "$email$idProblematica", idEscrito)
        return when (resultado) {
            is Error -> Response.status(Response.Status.BAD_REQUEST).entity(resultado).build()
            else -> Response.ok(resultado).build()
        }
    }
}