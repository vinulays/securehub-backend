package org.securehub.authservice.service;

import lombok.RequiredArgsConstructor;
import org.securehub.authservice.dto.KeycloakTokenResponse;
import org.securehub.authservice.dto.LoginRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final WebClient webClient;

    @Value("${keycloak.token-url}")
    private String tokenUrl;

    @Value("${keycloak.client-id}")
    private String clientId;

    @Value("${keycloak.client-secret}")
    private String clientSecret;

    public KeycloakTokenResponse login(LoginRequest request) {

        return webClient.post()
                .uri(tokenUrl)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue(buildLoginBody(request))
                .retrieve()
                .bodyToMono(KeycloakTokenResponse.class)
                .block();
    }

    public KeycloakTokenResponse refresh(String refreshToken) {

        return webClient.post()
                .uri(tokenUrl)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue(buildRefreshTokenBody(refreshToken))
                .retrieve()
                .bodyToMono(KeycloakTokenResponse.class)
                .block();
    }

    private MultiValueMap<String, String> buildLoginBody(LoginRequest request) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();

        formData.add("grant_type", "password");
        formData.add("client_id", clientId);
        formData.add("client_secret", clientSecret);
        formData.add("username", request.username());
        formData.add("password", request.password());

        return formData;
    }

    private MultiValueMap<String, String> buildRefreshTokenBody(String refreshToken) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();

        formData.add("grant_type", "refresh_token");
        formData.add("client_id", clientId);
        formData.add("client_secret", clientSecret);
        formData.add("refresh_token", refreshToken);

        return formData;

    }
}
