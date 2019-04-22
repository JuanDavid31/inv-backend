package rest;

import dao.DaoInvitacion;
import entity.Invitacion;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/invitaciones")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class InvitacionResource {

    private final DaoInvitacion daoInvitacion;

    public InvitacionResource(DaoInvitacion daoInvitacion){
        this.daoInvitacion = daoInvitacion;
    }

    @POST
    public Response hacerInvitacion(Invitacion invitacion){
        Invitacion nuevaInvitacion = daoInvitacion.agregarInvitacion(invitacion);
        return nuevaInvitacion != null ?
                Response.ok(nuevaInvitacion).build() :
                Response.status(Response.Status.BAD_REQUEST).build();
    }

    //TODO: Posible paso de queryParam para saber si rechaza o acepta la invitación

    @PUT
    public Response aceptarInvitacion(Invitacion invitacion){
        boolean aceptoCorrectamente = daoInvitacion.aceptarInvitacion(invitacion);
        return aceptoCorrectamente ?
                Response.ok().build() :
                Response.status(Response.Status.BAD_REQUEST).build();
    }

    @DELETE
    public Response eliminarORechazarInvitacion(Invitacion invitacion){
        boolean seElimino = daoInvitacion.eliminarInvitacion(invitacion);
        return seElimino ?
                Response.ok().build() :
                Response.status(Response.Status.BAD_REQUEST).build();
    }
}
