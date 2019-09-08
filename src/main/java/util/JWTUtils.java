package util;

import entity.Persona;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneId;

public class JWTUtils {

    private String key;

    private final static String EMAIL_CLAIM = "email";

    public JWTUtils(String jwtKey){
        this.key = jwtKey;
    }

    public String darToken(Persona personaIdentificada) {
        LocalDate seisMesesDespues = LocalDate.now().plusMonths(6);
        java.util.Date fecha = Date.from(seisMesesDespues.atStartOfDay()
                .atZone(ZoneId.systemDefault())
                .toInstant());
        return Jwts.builder()
                .setSubject(personaIdentificada.nombres)
                .setExpiration(fecha)
                .claim(EMAIL_CLAIM, personaIdentificada.email)
                .signWith(darJwtKey())
                .compact();
    }

    public Claims darContenido(String token){
        try{
            return Jwts.parser()
                    .setSigningKey(darJwtKey())
                    .parseClaimsJws(token)
                    .getBody();
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