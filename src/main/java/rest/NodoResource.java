package rest;

import org.hibernate.validator.constraints.NotEmpty;
import usecase.FotoUseCase;
import usecase.NodoUseCase;

import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/nodos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class NodoResource {

    private final FotoUseCase fotoUseCase;
    private final NodoUseCase nodoUseCase;

    public NodoResource(FotoUseCase fotoUseCase, NodoUseCase daoNodo){
        this.fotoUseCase = fotoUseCase;
        this.nodoUseCase = daoNodo;
    }

    @Path("/{idNodo}")
    @PUT
    public Response editarNodo(@PathParam("idNodo") int idNodo,
                               @QueryParam("apadrinar") @NotNull Boolean apadrinar,
                               @QueryParam("id-padre") @NotNull int idPadre){
        boolean todoBien;
        if(apadrinar){
            todoBien = nodoUseCase.apadrinar(idNodo, idPadre);
        }else{
            todoBien = nodoUseCase.desApadrinar(idNodo);
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