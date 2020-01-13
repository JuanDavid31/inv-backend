package annotation.filter;

import io.jsonwebtoken.Claims;
import util.JWTUtils;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;

@VerificadorAuth
public class AuthFilter implements ContainerRequestFilter {

    private final static String AUTHORIZATION_HEADER = "Authorization";

    private final JWTUtils jwtUtils;

    public AuthFilter(JWTUtils jwtUtils){
        this.jwtUtils = jwtUtils;
    }

    @Override
    public void filter(ContainerRequestContext requestContext){
        String token = requestContext.getHeaderString(AUTHORIZATION_HEADER);
        if(token == null) throw new WebApplicationException(Response.Status.UNAUTHORIZED);
        Claims contenido = jwtUtils.darContenido(token);
        if(contenido == null)throw new WebApplicationException(Response.Status.UNAUTHORIZED);
    }
}