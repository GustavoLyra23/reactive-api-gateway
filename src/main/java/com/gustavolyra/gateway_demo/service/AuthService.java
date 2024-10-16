package com.gustavolyra.gateway_demo.service;

import com.gustavolyra.gateway_demo.models.dto.LoginDto;
import com.gustavolyra.gateway_demo.repositories.UserRepository;
import com.gustavolyra.gateway_demo.util.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class AuthService {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(JwtUtil jwtUtil, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Mono<String> login(LoginDto loginDto) {
        return userRepository.findByEmail(loginDto.username()).flatMap(user -> {
            if (passwordEncoder.matches(loginDto.password(), user.getPassword())) {
                return jwtUtil.createToken(user);
            } else {
                return Mono.error(new RuntimeException("Invalid credentials"));
            }
        });
    }


}






