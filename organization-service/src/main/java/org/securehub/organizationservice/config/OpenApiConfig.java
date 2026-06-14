package org.securehub.organizationservice.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.*;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI serviceOpenAPI() {
        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes("keycloak",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.OAUTH2)
                                        .flows(new OAuthFlows()
                                                .authorizationCode(new OAuthFlow()
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

    @Bean
    public OpenApiCustomizer serverUrlCustomizer() {


        return openApi -> openApi.servers(List.of(
                new Server().url("http://localhost:8080").description("API Gateway")
        ));
    }
}
