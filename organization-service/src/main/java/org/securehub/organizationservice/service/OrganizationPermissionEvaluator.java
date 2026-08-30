package org.securehub.organizationservice.service;

import lombok.RequiredArgsConstructor;
import org.securehub.organizationservice.enums.OrganizationPermission;
import org.securehub.security.util.JwtUtils;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component("orgAuth")
@RequiredArgsConstructor
public class OrganizationPermissionEvaluator {

    private final OrganizationAuthorizationService authorizationService;
    private final UserLookupService userLookupService;

    public boolean hasPermission(
            UUID organizationId,
            OrganizationPermission permission
    ) {

        if (JwtUtils.hasRole("ADMIN")) {
            return true;
        }

        String keycloakUserId = JwtUtils.getUserKeycloakId();

        UUID userId = userLookupService.getUserByKeycloakUserId(keycloakUserId).id();

        return authorizationService.hasPermission(
                organizationId,
                userId,
                permission
        );
    }
}
