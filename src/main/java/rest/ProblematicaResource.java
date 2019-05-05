package rest;

import dao.DaoGrupo;
import dao.DaoInvitacion;
import dao.DaoProblematica;
import entity.Grupo;
import entity.Invitacion;
import entity.Nodo;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.hibernate.validator.constraints.NotEmpty;
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
    private final DaoGrupo daoGrupo;
    private final FotoUseCase fotoUseCase;

    public ProblematicaResource(DaoProblematica daoProblematica, DaoInvitacion daoInvitacion, DaoGrupo daoGrupo, FotoUseCase fotoUseCase){
        this.daoProblematica = daoProblematica;
        this.daoInvitacion = daoInvitacion;
        this.daoGrupo = daoGrupo;
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

    //TODO: Tal vez este metodo deba retornar muchas más información, como todos los nodos que contiene cada grupo

    @Path("/{idProblematica}/grupos")
    @GET
    public Response darGrupos(@PathParam("idProblematica") int idProblematica){
        List<Grupo> grupos = daoGrupo.darGrupos(idProblematica);
        return Response.ok(grupos).build();
    }

    //TODO: Probablemente esto sea reemplazado por los Websockets

    @POST
    @Path("/{idProblematica}/grupos")
    public Response agregarGrupo(@PathParam("idProblematica") int idProblematica, Grupo grupo){
        Grupo nuevoGrupo = daoGrupo.agregarGrupo(idProblematica, grupo);
        return Response.ok(nuevoGrupo).build();
    }

    /*@Path("/{idProblematica}/grupos/{idGrupo}")
    @PUT
    public Response cambiarNombreGrupo(@PathParam("idProblematica") int idProblematica,
                                       @PathParam("idGrupo") int idGrupo,
                                       Grupo grupo){
        boolean seActualizo = daoGrupo.actualizarGrupo(idGrupo, grupo);
        return seActualizo ?
                Response.ok(seActualizo).build() :
                Response.status(Response.Status.BAD_REQUEST).build();
    }

    @Path("/{idProblematica}/grupos/{idGrupo}")
    @PUT
    public Response apadrinarGrupo(@PathParam("idProblematica") int idProblematica,
                                   @PathParam("idGrupo") int idGrupo,
                                   @QueryParam("apadrinar") @NotEmpty Boolean apadrinar,
                                   int idPadre){
        boolean todoBien;
        if(apadrinar){
            todoBien = daoGrupo.apadrinar(idGrupo, idPadre, idProblematica);
        }else{
            todoBien = daoGrupo.desApadrinar(idGrupo, idProblematica);
        }
        return todoBien ?
                Response.ok().build() :
                Response.status(Response.Status.BAD_REQUEST).build();
    }*/

    @Path("/{idProblematica}/grupos/{idGrupo}")
    @DELETE
    public Response eliminarGrupo(@PathParam("idProblematica") int idProblematica,
                                  @PathParam("idGrupo") int idGrupo){
        boolean seElimino = daoGrupo.eliminarGrupo(idGrupo, idProblematica);
        return seElimino ?
                Response.ok().build() :
                Response.status(Response.Status.BAD_REQUEST).build();
    }

    @GET
    @Path("/{idProblematica}/reacciones")
    public Response darReacciones(@PathParam("idProblematica") int idProblematica){
        List<Grupo> grupos = daoGrupo.darGruposConReacciones(idProblematica);
        return Response.ok(grupos).build();
    }

}