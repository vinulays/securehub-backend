package com.securehub.api_gateway.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI secureHubOpenAPI() {

        return new OpenAPI()
                .info(new Info()
                        .title("SecureHub API Gateway")
                        .version("1.0")
                )
                .components(new Components()
                        .addSecuritySchemes("keycloak",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.OAUTH2)
                                        .flows(new OAuthFlows()
                                                .authorizationCode(
                                                        new OAuthFlow()
                                                                .authorizationUrl(
                                                                        "http://localhost:8081/realms/securehub/protocol/openid-connect/auth"
                                                                )
                                                                .tokenUrl(
                                                                        "http://localhost:8081/realms/securehub/protocol/openid-connect/token"
                                                                )
                                                                .scopes(new Scopes()
                                                                        .addString("openid", "OpenID")
                                                                        .addString("profile", "User profile")
                                                                        .addString("email", "User email")
                                                                )
                                                )
                                        )
                        )
                )
                .addSecurityItem(new SecurityRequirement().addList("keycloak"));
    }
}
