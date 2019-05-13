package rest;

import dao.DaoNodo;
import org.hibernate.validator.constraints.NotEmpty;
import usecase.FotoUseCase;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/nodos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class NodoResource {

    private final FotoUseCase fotoUseCase;
    private final DaoNodo daoNodo;

    public NodoResource(FotoUseCase fotoUseCase, DaoNodo daoNodo){
        this.fotoUseCase = fotoUseCase;
        this.daoNodo = daoNodo;
    }

    @Path("/{idNodo}")
    @PUT
    public Response editarNodo(@PathParam("idNodo") int idNodo,
                               @QueryParam("apadrinar") @NotEmpty Boolean apadrinar,
                               @QueryParam("id-padre") @NotEmpty int idPadre){
        boolean todoBien;
        if(apadrinar){
            todoBien = daoNodo.apadrinar(idNodo, idPadre);
        }else{
            todoBien = daoNodo.desApadrinar(idNodo);
        }
        return todoBien ?
                Response.ok().build() :
                Response.status(Response.Status.BAD_REQUEST).build();
    }

    @Path("/{idNodo}")
    @DELETE
    public Response eliminarNodo(@PathParam("idNodo") int idNodo){
        boolean seElimino = fotoUseCase.eliminarNodoYFoto(idNodo);
        return seElimino ?
                Response.ok().build() :
                Response.status(Response.Status.BAD_REQUEST).build();
    }
}