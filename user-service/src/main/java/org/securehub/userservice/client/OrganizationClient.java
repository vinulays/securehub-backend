package org.securehub.userservice.client;

import org.securehub.userservice.config.FeignConfig;
import org.securehub.userservice.dto.CreateMembershipRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

@FeignClient(name = "organization-service", configuration = FeignConfig.class)
public interface OrganizationClient {

    @PostMapping("/api/organizations/{organizationId}/members")
    void addMember(
            @PathVariable UUID organizationId,
            @RequestBody CreateMembershipRequest request
    );
}
