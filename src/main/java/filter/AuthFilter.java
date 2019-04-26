package filter;

import io.jsonwebtoken.Claims;
import util.JWTUtils;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import java.io.IOException;

@VerificadorAuth
public class AuthFilter implements ContainerRequestFilter {

    private final static String AUTHORIZATION_HEADER = "Authorization";

    private final JWTUtils jwtUtils;

    public AuthFilter(JWTUtils jwtUtils){
        this.jwtUtils = jwtUtils;
    }

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String token = requestContext.getHeaderString(AUTHORIZATION_HEADER);
        Claims contenido = jwtUtils.darContenido(token);
        if(contenido == null)throw new WebApplicationException(Response.Status.UNAUTHORIZED);
        //TODO: Ir modificando de acuerdo a las necesidades. Necesito conocer todas las posibles consultas.
    }
}