package com.gustavolyra.gateway_demo.config;

import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import reactor.core.publisher.Mono;

public class CustomReactiveAuthManager implements ReactiveAuthenticationManager {

    private final ReactiveUserDetailsService service;
    private final PasswordEncoder passwordEncoder;

    public CustomReactiveAuthManager(ReactiveUserDetailsService service, PasswordEncoder passwordEncoder) {
        this.service = service;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        return service.findByUsername(authentication.getName())
                .flatMap(userDetails -> {
                    if (passwordEncoder.matches(authentication.getCredentials().toString(), userDetails.getPassword())) {
                        return Mono.just(new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities()));
                    } else {
                        return Mono.empty();
                    }
                });
    }
}
