package rest;

import dao.DaoInvitacion;
import dao.DaoProblematica;
import entity.Invitacion;
import entity.Persona;
import entity.Problematica;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/problematicas")
@Consumes(MediaType.APPLICATION_JSON)
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


}
