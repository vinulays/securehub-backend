package org.securehub.authservice.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.securehub.authservice.dto.KeycloakTokenResponse;
import org.securehub.authservice.dto.LoginRequest;
import org.securehub.authservice.service.AuthService;
import org.securehub.authservice.util.CookieUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtDecoder jwtDecoder;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request, HttpServletResponse response) {

        KeycloakTokenResponse token = authService.login(request);

        CookieUtil.addHttpOnlyCookie(response, "access_token", token.accessToken(), 3600);

        CookieUtil.addHttpOnlyCookie(response, "refresh_token", token.refreshToken(), 7 * 24 * 3600);

        return ResponseEntity.ok(Map.of("message", "Login successful"));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@CookieValue(value = "refresh_token", required = false) String refreshToken, HttpServletResponse response) {

        if (refreshToken == null) {
            return ResponseEntity.status(401).body("Missing refresh token");
        }

        KeycloakTokenResponse token = authService.refresh(refreshToken);

        CookieUtil.addHttpOnlyCookie(response, "access_token", token.accessToken(), 3600);

        return ResponseEntity.ok(Map.of("message", "Token refreshed"));
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(HttpServletRequest request) {

        String token = CookieUtil.extractToken(request, "access_token");

        if (token == null) {
            return ResponseEntity.status(401).body(Map.of("error", "No access token"));
        }

        Jwt jwt = jwtDecoder.decode(token);

        Map<String, Object> user = new HashMap<>();

        user.put("userId", jwt.getSubject());
        user.put("email", jwt.getClaimAsString("email"));
        user.put("name", jwt.getClaimAsString("name"));
        user.put("preferred_username", jwt.getClaimAsString("preferred_username"));

        Map<String, Object> realmAccess = jwt.getClaim("realm_access");

        if (realmAccess != null) {
            user.put("roles", realmAccess.get("roles"));
        }

        return ResponseEntity.ok(user);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {

        CookieUtil.clearCookie(response, "access_token");
        CookieUtil.clearCookie(response, "refresh_token");

        return ResponseEntity.ok(Map.of("message", "Logged out"));
    }
}
