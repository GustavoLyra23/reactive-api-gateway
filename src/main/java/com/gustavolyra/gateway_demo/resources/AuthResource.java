package com.gustavolyra.gateway_demo.resources;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/auth")
public class AuthResource {

    @PostMapping("/login")
    public String login() {
        return "Login";
    }





}
