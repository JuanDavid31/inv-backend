package rest;

import usecase.ReaccionUseCase;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/grupos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class GrupoResource {

    private final ReaccionUseCase daoReaccion;

    public GrupoResource(ReaccionUseCase ReaccionUseCase){
        this.daoReaccion = ReaccionUseCase;
    }

    @POST
    @Path("/{idGrupo}/reacciones")
    public Response reaccionar(@PathParam("idGrupo") int idGrupo,
                               @QueryParam("valor") int valor,
                               @QueryParam("id-persona-problematica") String idPersonaProblematica){
        boolean seReacciono = daoReaccion.reaccionar(valor, idGrupo, idPersonaProblematica);
        return seReacciono ?
                Response.ok().build() :
                Response.status(Response.Status.BAD_REQUEST).build();
    }
}