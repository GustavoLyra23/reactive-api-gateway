package com.gustavolyra.gateway_demo.models.entities;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {

    USER, ADMIN;

    @Override
    public String getAuthority() {
        return "ROLE_" + this.name();
    }
}
