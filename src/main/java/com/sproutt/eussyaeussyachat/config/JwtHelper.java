package com.sproutt.eussyaeussyachat.config;


import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtHelper {

    @Value("${jwt.secret}")
    private String secretKey;

    public boolean validateToken(String jwt) {
        return this.getClaims(jwt) != null;
    }

    private Jws<Claims> getClaims(String token) {
        try {
            Jws<Claims> claims = Jwts.parser()
                                     .setSigningKey(secretKey)
                                     .parseClaimsJws(token);
            return claims;
        } catch (Exception e) {
            throw new UnsupportedJwtException("token parser fail");
        }
    }
}