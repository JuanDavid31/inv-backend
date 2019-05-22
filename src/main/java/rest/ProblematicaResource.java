package rest;

import entity.Invitacion;
import org.hibernate.validator.constraints.NotEmpty;
import usecase.ProblematicaUseCase;
import usecase.InvitacionUseCase;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/problematicas")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ProblematicaResource {

    private final InvitacionUseCase InvitacionUseCase;
    private final ProblematicaUseCase problematicaUseCase;

    public ProblematicaResource(InvitacionUseCase invitacionUseCase, ProblematicaUseCase problematicaUseCase){
        this.InvitacionUseCase = invitacionUseCase;
        this.problematicaUseCase = problematicaUseCase;
    }

    @Path("/{idProblematica}/personas/{emailRemitente}/invitaciones")
    @GET
    public Response darPersonasInvitadas(@PathParam("idProblematica") int idProblematica,
                                         @PathParam("emailRemitente") String emailRemitente){
        List<Invitacion> personas = InvitacionUseCase.darPersonasInvitadas(emailRemitente, idProblematica);
        return Response.ok(personas).build();
    }

    @Path("/{idProblematica}")
    @POST
    public Response avanzarFase(@PathParam("idProblematica") int idProblematica,
                                @QueryParam("avanzar") @NotEmpty Boolean avanzar){
        boolean seAvanzo = problematicaUseCase.avanzarFase(idProblematica);
        return seAvanzo ?
                Response.ok().build():
                Response.status(Response.Status.BAD_REQUEST).build();
    }


}