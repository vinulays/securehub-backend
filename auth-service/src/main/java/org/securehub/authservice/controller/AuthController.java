package org.securehub.authservice.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.securehub.authservice.dto.KeycloakTokenResponse;
import org.securehub.authservice.dto.LoginRequest;
import org.securehub.authservice.service.AuthService;
import org.securehub.authservice.util.CookieUtil;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public String login(
            @RequestBody LoginRequest request,
            HttpServletResponse response
    ) {

        KeycloakTokenResponse token = authService.login(request);

        CookieUtil.addHttpOnlyCookie(
                response,
                "access_token",
                token.accessToken(),
                3600
        );

        CookieUtil.addHttpOnlyCookie(
                response,
                "refresh_token",
                token.refreshToken(),
                7 * 24 * 3600
        );

        return "Login successful";
    }

    @PostMapping("/refresh")
    public String refresh(
            @CookieValue("refresh_token") String refreshToken,
            HttpServletResponse response
    ) {

        KeycloakTokenResponse token = authService.refresh(refreshToken);

        CookieUtil.addHttpOnlyCookie(
                response,
                "access_token",
                token.accessToken(),
                3600
        );

        return "Token refreshed";
    }

    @PostMapping("/logout")
    public String logout(HttpServletResponse response) {

        CookieUtil.clearCookie(response, "access_token");
        CookieUtil.clearCookie(response, "refresh_token");

        return "Logged out";
    }
}
