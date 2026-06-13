package org.securehub.authservice.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.securehub.authservice.dto.KeycloakTokenResponse;
import org.securehub.authservice.dto.LoginRequest;
import org.securehub.authservice.service.AuthService;
import org.securehub.authservice.util.CookieUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request, HttpServletResponse response) {

        KeycloakTokenResponse token = authService.login(request);

        CookieUtil.addHttpOnlyCookie(response, "access_token", token.accessToken(), 3600);

        CookieUtil.addHttpOnlyCookie(response, "refresh_token", token.refreshToken(), 7 * 24 * 3600);

        return ResponseEntity.ok(Map.of("message", "Login successful"));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@CookieValue(value = "refresh_token", required = false) String refreshToken, HttpServletResponse response) {

        KeycloakTokenResponse token = authService.refresh(refreshToken);

        CookieUtil.addHttpOnlyCookie(response, "access_token", token.accessToken(), 3600);

        return ResponseEntity.ok(Map.of("message", "Token refreshed"));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {

        CookieUtil.clearCookie(response, "access_token");
        CookieUtil.clearCookie(response, "refresh_token");

        return ResponseEntity.ok(Map.of("message", "Logged out"));
    }
}
