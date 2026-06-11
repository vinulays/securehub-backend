package org.securehub.organizationservice.client;

import org.securehub.organizationservice.config.FeignConfig;
import org.securehub.organizationservice.dto.UserBatchRequest;
import org.securehub.organizationservice.dto.UserBatchResponse;
import org.securehub.organizationservice.dto.UserSummaryResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

@FeignClient(name = "user-service", configuration = FeignConfig.class)
public interface UserClient {

    @PostMapping("/api/internal/users/batch")
    UserBatchResponse getUsersBatch(@RequestBody UserBatchRequest request);

    @GetMapping("/api/internal/users/{id}")
    UserSummaryResponse getUser(@PathVariable UUID id);
}
