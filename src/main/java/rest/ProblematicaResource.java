package rest;

import dao.DaoInvitacion;
import dao.DaoProblematica;
import entity.Invitacion;
import entity.Nodo;
import org.glassfish.jersey.media.multipart.FormDataParam;
import usecase.FotoUseCase;

import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.util.List;

@Path("/problematicas")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ProblematicaResource {

    private final DaoProblematica daoProblematica;
    private final DaoInvitacion daoInvitacion;
    private final FotoUseCase fotoUseCase;

    public ProblematicaResource(DaoProblematica daoProblematica, DaoInvitacion daoInvitacion, FotoUseCase fotoUseCase){
        this.daoProblematica = daoProblematica;
        this.daoInvitacion = daoInvitacion;
        this.fotoUseCase = fotoUseCase;
    }

    @Path("/{idProblematica}/personas/{emailRemitente}/invitaciones")
    @GET
    public Response darPersonasInvitadas(@PathParam("idProblematica") int idProblematica, @PathParam("emailRemitente") String emailRemitente){
        List<Invitacion> personas = daoInvitacion.darPersonasInvitadas(emailRemitente, idProblematica);
        return Response.ok(personas).build();
    }

    //TODO: A esto le falta un queryParam
    @Path("/{idProblematica}")
    @POST
    public Response avanzarFase(@PathParam("idProblematica") int idProblematica){
        boolean seAvanzo = daoProblematica.avanzarFaseProblematica(idProblematica);
        return seAvanzo ?
                Response.ok().build():
                Response.status(Response.Status.BAD_REQUEST).build();
    }

    @Path("/{idProblematica}/nodos")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @POST
    public Response subirNodo(@NotNull @HeaderParam("extension") String extensionFoto,
                               @NotNull @FormDataParam("foto") InputStream foto,
                               @NotNull @FormDataParam("email") String email,
                               @PathParam("idProblematica") int idProblematica){

        Nodo nodo = fotoUseCase.guardarFoto(new Nodo(email, idProblematica), foto, extensionFoto);
        return nodo != null ?
                Response.ok(nodo).build() :
                Response.status(Response.Status.BAD_REQUEST).build();
    }
}