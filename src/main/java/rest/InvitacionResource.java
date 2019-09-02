package rest;

import dao.DaoInvitacion;
import entity.Invitacion;
import org.hibernate.validator.constraints.NotEmpty;
import usecase.InvitacionUseCase;

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
    private final InvitacionUseCase invitacionUseCase;

    public InvitacionResource(DaoInvitacion daoInvitacion, InvitacionUseCase invitacionUseCase){
        this.daoInvitacion = daoInvitacion;
        this.invitacionUseCase = invitacionUseCase;
    }

    @POST
    public Response hacerInvitacion(@Valid @NotNull Invitacion invitacion){
        Object nuevaInvitacion = invitacionUseCase.hacerInvitacion(invitacion);
        return nuevaInvitacion != null ?
                Response.ok(nuevaInvitacion).build() :
                Response.status(Response.Status.BAD_REQUEST).build();
    }

    @PUT
    @Path("/{idInvitacion}")
    public Response responderInvitacion(@PathParam("idInvitacion") String idInvitacion,
                                        @Valid
                                        @NotNull Invitacion invitacion,
                                        @QueryParam("aceptar")
                                        @NotEmpty
                                        @NotNull Boolean acepto){
        boolean operacionExitosa;
        if(acepto){
            operacionExitosa = daoInvitacion.aceptarInvitacion(invitacion, idInvitacion);
        }else{
            operacionExitosa = daoInvitacion.rechazarInvitacion(invitacion, idInvitacion);
        }
        return operacionExitosa ?
                Response.ok().build() :
                Response.status(Response.Status.BAD_REQUEST).build();
    }

    @DELETE
    @Path("/{idInvitacion}")
    public Response eliminarInvitacion(@PathParam("idInvitacion") String idInvitacion,
                                        @Valid @NotNull Invitacion invitacion){
        boolean seElimino = daoInvitacion.eliminarInvitacion(invitacion, idInvitacion);
        return seElimino ?
                Response.ok().build() :
                Response.status(Response.Status.BAD_REQUEST).build();
    }
}