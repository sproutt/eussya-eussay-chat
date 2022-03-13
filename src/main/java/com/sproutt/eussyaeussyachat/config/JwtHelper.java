package com.sproutt.eussyaeussyachat.config;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.sproutt.eussyaeussyachat.domain.member.MemberTokenCommand;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtHelper {

    @Value("${jwt.secret}")
    private String secretKey;

    private static final String CLAIM_KEY = "member";

    public boolean validateToken(String jwt) {
        return this.getClaims(jwt) != null;
    }

    public long getMemberIdFromToken(String token) {
        Jws<Claims> claims = getClaims(token);
        ObjectMapper objectMapper = new ObjectMapper();

        return objectMapper.convertValue(claims.getBody().get(CLAIM_KEY), MemberTokenCommand.class).getId();
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