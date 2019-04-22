package rest;

import dao.DaoInvitacion;
import dao.DaoPersona;
import dao.DaoProblematica;
import entity.Persona;
import entity.Problematica;
import filter.VerificadorAuth;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;

@Path("/personas")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PersonaResource {

    private final DaoPersona daoPersona;
    private final DaoProblematica daoProblematica;
    private final DaoInvitacion daoInvitacion;

    public PersonaResource(DaoPersona daoPersona, DaoProblematica daoProblematica, DaoInvitacion daoInvitacion) {
        this.daoPersona = daoPersona;
        this.daoProblematica = daoProblematica;
        this.daoInvitacion = daoInvitacion;
    }

    @POST
    public Response agregarPersona(@Valid @NotNull Persona persona){
        Persona personaAgregada = daoPersona.agregarPersona(persona);
        return Response.ok(personaAgregada).build();
    }

    @GET
    @Path("/{email}/problematicas")
    @VerificadorAuth
    public Response darProblematicasPorPersona(@PathParam("email") String email){
        List<Problematica> problematicas = daoProblematica.darProblematicasPorPersona(email);
        return Response.ok(problematicas).build();
    }

    @POST
    @Path("/{email}/problematicas")
    @VerificadorAuth
    public Response agregarProblematicaPorPersona(@PathParam("email") String email,
                                                  Problematica problematica){
        Problematica nuevaProblematica;
        try{
            nuevaProblematica = daoProblematica.agregarProblematicaPorPersona(email, problematica);
        }catch (Exception e){
            e.printStackTrace();
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        return Response.ok(nuevaProblematica).build();
    }

    @GET
    @Path("/{email}/invitaciones")
    public Response darInvitacionesVigentes(@PathParam("email") String email){
        List<Map<String, Object>> invitaciones = daoInvitacion.darInvitacionesVigentes(email);
        return Response.ok(invitaciones).build();
    }

    @GET
    @Path("/tabla/crear")
    public Response crearTabla(){
        daoPersona.eliminarTablas();
        daoPersona.crearTable();
        return Response.ok("Eliminada y creada").build();
    }

}