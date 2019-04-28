package rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("/nodos")
public class NodoResource {

    @GET
    public Response nada(){
        return null;
    }
}
