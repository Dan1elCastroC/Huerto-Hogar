package com.huerto.hogar.security.jwt;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {
    private final Key key;
    private final long expirationMs;

    public JwtUtil(@Value("${jwt.secret}") String secret,
                   @Value("${jwt.expiration-ms}") long expirationMs) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMs = expirationMs;
    }

    public String generarToken(String correo, String rol) {
        return Jwts.builder()
            .setSubject(correo)
            .claim("rol", rol)
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
            .signWith(key, SignatureAlgorithm.HS256)
            .compact();
    }

    public String extraerCorreo(String token) { return parsear(token).getBody().getSubject(); }
    public String extraerRol(String token)    { return parsear(token).getBody().get("rol", String.class); }

    public boolean esValido(String token) {
        try { parsear(token); return true; }
        catch (JwtException | IllegalArgumentException e) { return false; }
    }

    private Jws<Claims> parsear(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
    }
}
