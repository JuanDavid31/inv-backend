package rest;

import dao.DaoPersona;
import entity.Persona;
import org.hibernate.validator.constraints.NotEmpty;
import usecase.CorreoUseCase;
import util.JWTUtils;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/auth")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AuthResource {

    private final DaoPersona daoPersona;

    private final JWTUtils jwtUtils;

    private final CorreoUseCase correoUseCase;

    public AuthResource(DaoPersona daoPersona, JWTUtils jwtUtils, CorreoUseCase correoUtils){
        this.daoPersona = daoPersona;
        this.jwtUtils = jwtUtils;
        this.correoUseCase = correoUtils;
    }

    @POST
    public Response credencialesCorrectas(Persona persona){
        Persona personaIdentificada = daoPersona.darPersonaPorCredenciales(persona);
        return personaIdentificada != null?
                Response.ok(jwtUtils.darToken(personaIdentificada)).build() :
                Response.status(Response.Status.BAD_REQUEST).build();
    }

    @POST
    @Path("/pass")
    public Response olvideContraseña(@QueryParam("email") @NotEmpty String email){
        boolean envioExitoso = correoUseCase.enviarCorreo(email);
        return envioExitoso ?
                Response.ok().build() :
                Response.status(Response.Status.BAD_REQUEST).build();
    }
}