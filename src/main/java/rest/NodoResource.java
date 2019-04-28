package rest;

import usecase.FotoUseCase;

import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

@Path("/nodos")
public class NodoResource {

    private final FotoUseCase fotoUseCase;


    public NodoResource(FotoUseCase fotoUseCase){
        this.fotoUseCase = fotoUseCase;
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
