package com.gustavolyra.gateway_demo.config;

import com.gustavolyra.gateway_demo.util.JwtUtil;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import reactor.core.publisher.Mono;

public class JwtAuthenticationManager implements ReactiveAuthenticationManager {
    private final JwtUtil jwtUtil;
    private final ReactiveUserDetailsService userDetailsService;

    public JwtAuthenticationManager(JwtUtil jwtUtil, ReactiveUserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        String token = authentication.getCredentials().toString();

        return jwtUtil.getUsernameFromToken(token)
                .flatMap(username -> jwtUtil.validateToken(token)
                        .flatMap(isValid -> {
                            if (!isValid) {
                                return Mono.empty();
                            }
                            return userDetailsService.findByUsername(username)
                                    .map(userDetails -> new UsernamePasswordAuthenticationToken(
                                            userDetails,
                                            null,
                                            userDetails.getAuthorities()));
                        }));
    }
}
