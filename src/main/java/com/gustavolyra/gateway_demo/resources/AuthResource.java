package com.gustavolyra.gateway_demo.resources;

import com.gustavolyra.gateway_demo.models.dto.LoginDto;
import com.gustavolyra.gateway_demo.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/v1/auth")
public class AuthResource {

    private final AuthService authService;

    public AuthResource(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public Mono<ResponseEntity<Map<String, String>>> login(@RequestBody LoginDto loginDto) {
        return authService.login(loginDto).map(token ->
                ResponseEntity.ok(Map.of("token", token)));
    }





}
