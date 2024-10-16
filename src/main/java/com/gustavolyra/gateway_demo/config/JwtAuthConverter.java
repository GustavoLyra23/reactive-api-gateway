package com.gustavolyra.gateway_demo.config;

import com.gustavolyra.gateway_demo.service.ReactiveAuthService;
import com.gustavolyra.gateway_demo.util.JwtUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class JwtAuthConverter {

    private final JwtUtil jwtUtil;
    private final ReactiveAuthService authService;

    public JwtAuthConverter(JwtUtil jwtUtil, ReactiveAuthService authService) {
        this.jwtUtil = jwtUtil;
        this.authService = authService;
    }

    public Mono<Authentication> convert(ServerWebExchange exchange) {
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return Mono.empty();
        }

        String token = authHeader.replace("Bearer ", "");
        return jwtUtil.extractUsername(token)
                .flatMap(username -> authService.findByUsername(username)
                        .flatMap(userDetails -> jwtUtil.validateToken(token, userDetails)
                                .flatMap(isValid -> {
                                    if (Boolean.TRUE.equals(isValid)) {
                                        return Mono.just(new UsernamePasswordAuthenticationToken(
                                                userDetails, null, userDetails.getAuthorities()));
                                    } else {
                                        return Mono.empty();
                                    }
                                })
                        )
                );
    }
}
