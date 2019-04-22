package rest;

import dao.DaoPersona;
import entity.Persona;
import io.jsonwebtoken.Jwt;
import util.JWTUtils;

import javax.print.attribute.standard.Media;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/auth")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AuthResource {

    private final DaoPersona daoPersona;

    private final JWTUtils jwtUtils;

    public AuthResource(DaoPersona daoPersona, JWTUtils jwtUtils){
        this.daoPersona = daoPersona;
        this.jwtUtils = jwtUtils;
    }

    @POST
    public Response credencialesCorrectas(Persona persona){
        Persona personaIdentificada = daoPersona.darPersonaPorCredenciales(persona);
        return personaIdentificada != null?
                Response.ok(jwtUtils.darToken(personaIdentificada)).build() :
                Response.status(Response.Status.BAD_REQUEST).build();
    }
}