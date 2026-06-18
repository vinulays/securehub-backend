package org.securehub.organizationservice.service;

import lombok.RequiredArgsConstructor;
import org.securehub.organizationservice.entity.OrganizationMembership;
import org.securehub.organizationservice.enums.OrganizationPermission;
import org.securehub.organizationservice.enums.OrganizationRolePermissionMapping;
import org.securehub.organizationservice.exception.OrganizationAccessDeniedException;
import org.securehub.organizationservice.repository.MembershipRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrganizationAuthorizationService {

    private final MembershipRepository membershipRepository;

    public boolean hasPermission(
            UUID organizationId,
            UUID userId,
            OrganizationPermission permission
    ) {

        return membershipRepository
                .findByOrganizationIdAndUserId(organizationId, userId)
                .map(OrganizationMembership::getRole)
                .map(role -> OrganizationRolePermissionMapping.ROLE_PERMISSIONS
                        .getOrDefault(role, List.of()))
                .map(permissions -> permissions.contains(permission))
                .orElse(false);
    }

    public void validateOrganizationAccess(UUID organizationId, UUID userId) {

        boolean hasAccess = membershipRepository.existsByOrganizationIdAndUserIdAndIsActiveTrue(
                organizationId,
                userId
        );

        if (!hasAccess) {
            throw new OrganizationAccessDeniedException("You don't have access to this organization");
        }
    }
}
