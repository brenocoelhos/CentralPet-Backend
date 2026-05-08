package br.com.cetral.centralpet.service;

import br.com.cetral.centralpet.model.Usuario;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class JwtService {

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expiration-ms:86400000}")
    private long jwtExpirationMs;

    // Blacklist em memória — tokens invalidados por logout
    private final Set<String> blacklist = Collections.newSetFromMap(new ConcurrentHashMap<>());

    public String gerarToken(Usuario usuario) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + jwtExpirationMs);

        return Jwts.builder()
                .subject(usuario.getEmail())
                .issuedAt(now)
                .expiration(expiry)
                .signWith(getSignKey())
                .compact();
    }

    public String extrairEmail(String token) {
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("Token ausente");
        }
        if (blacklist.contains(token)) {
            throw new IllegalArgumentException("Token foi invalidado (logout)");
        }
        try {
            return Jwts.parser()
                    .verifyWith(getSignKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getSubject();
        } catch (ExpiredJwtException e) {
            throw new IllegalArgumentException("Token expirado", e);
        } catch (JwtException e) {
            throw new IllegalArgumentException("Token inválido: " + e.getMessage(), e);
        }
    }

    public boolean tokenValido(String token, String email) {
        try {
            return email.equals(extrairEmail(token));
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }

    public void invalidarToken(String token) {
        blacklist.add(token);
    }

    public long getJwtExpirationMs() {
        return jwtExpirationMs;
    }

    private SecretKey getSignKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }
}
