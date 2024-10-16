package com.gustavolyra.gateway_demo.config;

import com.gustavolyra.gateway_demo.service.ReactiveAuthService;
import com.gustavolyra.gateway_demo.util.JwtUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Component
public class JwtAuthFilter implements WebFilter {

    private final JwtUtil jwtUtil;
    private final ReactiveAuthService authService;

    public JwtAuthFilter(JwtUtil jwtUtil, ReactiveAuthService authService) {
        this.jwtUtil = jwtUtil;
        this.authService = authService;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String token = Objects.requireNonNull(request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION))
                .replace("Bearer ", "");

        return jwtUtil.extractUsername(token)
                .flatMap(username -> authService.findByUsername(username)
                        .flatMap(userDetails -> jwtUtil.validateToken(token, userDetails)
                                .flatMap(isValid -> {
                                    if (!isValid) {
                                        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                                        return exchange.getResponse().setComplete();
                                    }
                                    return chain.filter(exchange);
                                })
                        )
                ).onErrorResume(e -> {
                    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                    return exchange.getResponse().setComplete();
                });
    }
}
