package com.gustavolyra.gateway_demo.service;

import com.gustavolyra.gateway_demo.repositories.UserRepository;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class ReactiveAuthService implements ReactiveUserDetailsService {

    private final UserRepository userRepository;

    public ReactiveAuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return userRepository.findByEmail(username).cast(UserDetails.class)
                .switchIfEmpty(Mono.error(new UsernameNotFoundException("User not found")));
    }

}
