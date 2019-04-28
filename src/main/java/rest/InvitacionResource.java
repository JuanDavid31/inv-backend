package rest;

import dao.DaoInvitacion;
import entity.Invitacion;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
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

    @PUT
    public Response responderInvitacion(@Valid @NotNull Invitacion invitacion,
                                        @QueryParam("aceptar") @NotEmpty Boolean acepto){
        boolean operacionExitosa;
        if(acepto){
            operacionExitosa = daoInvitacion.aceptarInvitacion(invitacion);
        }else{
            operacionExitosa = daoInvitacion.rechazarInvitacion(invitacion);
        }
        return operacionExitosa ?
                Response.ok().build() :
                Response.status(Response.Status.BAD_REQUEST).build();
    }

    @DELETE
    public Response eliminarInvitacion(@Valid @NotNull Invitacion invitacion){
        boolean seElimino = daoInvitacion.eliminarInvitacion(invitacion);
        return seElimino ?
                Response.ok().build() :
                Response.status(Response.Status.BAD_REQUEST).build();
    }
}
