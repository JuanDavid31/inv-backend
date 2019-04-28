package rest;

import com.codahale.metrics.annotation.Timed;
import dao.DaoInvitacion;
import dao.DaoProblematica;
import entity.Invitacion;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.FormDataParam;


import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@Path("/problematicas")
/*@Consumes(MediaType.APPLICATION_JSON)*/
@Produces(MediaType.APPLICATION_JSON)
public class ProblematicaResource {

    private final DaoProblematica daoProblematica;
    private final DaoInvitacion daoInvitacion;

    public ProblematicaResource(DaoProblematica daoProblematica, DaoInvitacion daoInvitacion){
        this.daoProblematica = daoProblematica;
        this.daoInvitacion = daoInvitacion;
    }

    @Path("/{idProblematica}/personas/{emailRemitente}/invitaciones")
    @GET
    public Response darPersonasInvitadas(@PathParam("idProblematica") int idProblematica, @PathParam("emailRemitente") String emailRemitente){
        List<Invitacion> personas = daoInvitacion.darPersonasInvitadas(emailRemitente, idProblematica);
        return Response.ok(personas).build();
    }

    //TODO: A esto le falta algo
    @Path("/{idProblematica}")
    @POST
    public Response avanzarFase(@PathParam("idProblematica") int idProblematica){
        boolean seAvanzo = daoProblematica.avanzarFaseProblematica(idProblematica);
        return seAvanzo ?
                Response.ok().build():
                Response.status(Response.Status.BAD_REQUEST).build();
    }

    @Path("/{idProblematica}/nodos")
    @Consumes({MediaType.MULTIPART_FORM_DATA, MediaType.APPLICATION_JSON})
    @POST
    public Response subirNodo(FormDataMultiPart foto){
/*        try {
            *//*Files.copy(foto, Paths.get("~/fotos/foto.png"));*//*
        } catch (IOException e) {
            e.printStackTrace();
            return Response.status(Response.Status.BAD_REQUEST).build();
        }*/
        return Response.ok().build();
    }

    @Path("/{idProblematica}/nodos1")
    @Consumes({MediaType.MULTIPART_FORM_DATA, MediaType.APPLICATION_JSON})
    @POST
    public Response subirNodo2(@FormDataParam("foto") InputStream foto){
        try {
             Files.copy(foto, Paths.get("~/fotos/foto.png"));
        } catch (IOException e) {
            e.printStackTrace();
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        return Response.ok().build();
    }

}