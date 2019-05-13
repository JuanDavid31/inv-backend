package rest;

import dao.DaoInvitacion;
import dao.DaoNodo;
import dao.DaoPersona;
import dao.DaoProblematica;
import entity.Nodo;
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
    private final DaoNodo daoNodo;

    public PersonaResource(DaoPersona daoPersona, DaoProblematica daoProblematica, DaoNodo daoNodo) {
        this.daoPersona = daoPersona;
        this.daoProblematica = daoProblematica;
        this.daoNodo = daoNodo;
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
    @Path("/{email}/problematicas/{idProblematica}/nodos")
    public Response darNodos(@PathParam("email") String email,
                             @PathParam("idProblematica") Integer idProblematica){
        List<Nodo> nodos = daoNodo.darNodos(email + idProblematica);
        return Response.ok(nodos).build();
    }
}