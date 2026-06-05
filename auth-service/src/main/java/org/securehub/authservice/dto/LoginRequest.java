package org.securehub.authservice.dto;

public record LoginRequest(
        String username, String password
) {
}
