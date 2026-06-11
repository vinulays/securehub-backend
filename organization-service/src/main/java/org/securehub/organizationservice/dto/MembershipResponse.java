package org.securehub.organizationservice.dto;

import org.securehub.organizationservice.enums.OrganizationRole;

import java.util.UUID;

public record MembershipResponse(

        UUID id,

        UUID organizationId,

        UUID userId,

        OrganizationRole role,

        Boolean isActive
) {
}
