package com.hyrup.studentmanagement.security;

import com.hyrup.studentmanagement.config.JwtProperties;
import com.hyrup.studentmanagement.user.model.AppUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    private final JwtProperties jwtProperties;
    private SecretKey signingKey;

    public JwtService(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    @PostConstruct
    public void init() {
        byte[] keyBytes = jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < 32) {
            throw new IllegalStateException("JWT secret must be at least 32 bytes long for HS256");
        }
        this.signingKey = Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateAccessToken(AppUser user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("uid", user.getId());
        claims.put("email", user.getEmail());
        claims.put("role", user.getRole().name());
        claims.put("type", "access");

        return buildToken(claims, user.getEmail(), jwtProperties.getAccessTokenExpirationSeconds());
    }

    public String generateRefreshToken(AppUser user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("uid", user.getId());
        claims.put("type", "refresh");

        return buildToken(claims, user.getEmail(), jwtProperties.getRefreshTokenExpirationSeconds());
    }

    public boolean isAccessTokenValid(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        String type = extractTokenType(token);

        return username.equals(userDetails.getUsername()) && "access".equals(type) && !isTokenExpired(token);
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String extractTokenType(String token) {
        return extractClaim(token, claims -> claims.get("type", String.class));
    }

    public Long extractUserId(String token) {
        Object userId = extractClaim(token, claims -> claims.get("uid"));
        if (userId == null) {
            return null;
        }
        if (userId instanceof Number number) {
            return number.longValue();
        }
        return Long.parseLong(userId.toString());
    }

    public Instant extractExpiration(String token) {
        Date expiration = extractClaim(token, Claims::getExpiration);
        return expiration.toInstant();
    }

    public <T> T extractClaim(String token, Function<Claims, T> resolver) {
        Claims claims = extractAllClaims(token);
        return resolver.apply(claims);
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).isBefore(Instant.now());
    }

    private String buildToken(Map<String, Object> claims, String subject, long expirationInSeconds) {
        Instant now = Instant.now();

        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuer(jwtProperties.getIssuer())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(expirationInSeconds)))
                .signWith(signingKey, Jwts.SIG.HS256)
                .compact();
    }
}
