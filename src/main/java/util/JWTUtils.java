package util;

import entity.Persona;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import java.io.UnsupportedEncodingException;
import java.security.Key;

public class JWTUtils {

    private String key;

    public final static String EMAIL_CLAIM = "email";

    public JWTUtils(String jwtKey){
        this.key = jwtKey;
    }

    public String darToken(Persona personaIdentificada) { //TODO: Revisar la caducidad
        return Jwts.builder()
                .setSubject(personaIdentificada.nombre)
                .claim(EMAIL_CLAIM, personaIdentificada.email)
                .signWith(darJwtKey())
                .compact();
    }

    public Claims darContenido(String token){
        try{
            return Jwts.parser().setSigningKey(darJwtKey()).parseClaimsJws(token).getBody();
        }catch (JwtException e){
            e.printStackTrace();
            return null;
        }
    }

    private Key darJwtKey(){
        try {
            return Keys.hmacShaKeyFor(key.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }
}