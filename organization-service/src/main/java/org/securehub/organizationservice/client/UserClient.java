package org.securehub.organizationservice.client;

import org.securehub.organizationservice.config.FeignConfig;
import org.securehub.organizationservice.dto.UserSummaryResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "user-service", configuration = FeignConfig.class)
public interface UserClient {

    @GetMapping("/api/internal/users/{id}")
    UserSummaryResponse getUser(@PathVariable UUID id);
}
