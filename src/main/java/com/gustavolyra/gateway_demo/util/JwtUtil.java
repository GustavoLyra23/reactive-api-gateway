package com.gustavolyra.gateway_demo.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.stream.Collectors;

@Component
public class JwtUtil {

    private final SecretKey secretKey;

    public JwtUtil() {
        this.secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    }

    public Mono<String> createToken(UserDetails userDetails) {
        return Mono.fromCallable(() -> {
            Claims claims = Jwts.claims().setSubject(userDetails.getUsername());
            claims.put("roles", userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList()));
            long validityInMs = 3600000; // 1 hora de validade

            return Jwts.builder()
                    .setClaims(claims)
                    .setIssuedAt(Date.from(Instant.now()))
                    .setExpiration(new Date(System.currentTimeMillis() + validityInMs))
                    .signWith(secretKey)
                    .compact();
        });
    }

    public Mono<Boolean> validateToken(String token) {
        return Mono.fromCallable(() -> {
            try {
                Jwts.parserBuilder()
                        .setSigningKey(secretKey)
                        .build()
                        .parseClaimsJws(token);
                return true;
            } catch (Exception e) {
                return false;
            }
        });
    }

    public Mono<String> getUsernameFromToken(String token) {
        return Mono.fromCallable(() -> Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject());
    }
}
