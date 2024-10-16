package com.gustavolyra.gateway_demo.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String SECRET_KEY;

    public Mono<String> extractUsername(String token) {
        return extractAllClaims(token)
                .map(Claims::getSubject);
    }

    public Mono<Boolean> validateToken(String token, UserDetails userDetails) {
        return extractAllClaims(token)
                .map(claims -> {
                    String username = claims.getSubject();
                    return username.equals(userDetails.getUsername()) && !isTokenExpired(claims);
                })
                .onErrorReturn(false);
    }

    private Mono<Claims> extractAllClaims(String token) {
        return Mono.defer(() -> {
            try {
                return Mono.just(Jwts.parser()
                        .setSigningKey(SECRET_KEY)
                        .parseClaimsJws(token)
                        .getBody());
            } catch (SignatureException e) {
                return Mono.error(new RuntimeException("Invalid token signature", e));
            } catch (Exception e) {
                return Mono.error(new RuntimeException("Token validation error", e));
            }
        });
    }

    private boolean isTokenExpired(Claims claims) {
        return claims.getExpiration().before(new Date());
    }

    public Mono<String> generateToken(UserDetails userDetails) {
        Claims claims = Jwts.claims().setSubject(userDetails.getUsername());
        claims.put("roles", userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList());
        return Mono.defer(() -> Mono.just(
                Jwts.builder()
                        .setClaims(claims)
                        .setIssuedAt(new Date())
                        .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
                        .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                        .compact()
        ));
    }
}
