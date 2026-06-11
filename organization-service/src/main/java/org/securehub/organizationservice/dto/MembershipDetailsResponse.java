package org.securehub.organizationservice.dto;

import org.securehub.organizationservice.enums.OrganizationRole;

import java.util.UUID;

public record MembershipDetailsResponse(

        UUID membershipId,

        UUID userId,

        OrganizationRole role,

        boolean isActive,

        UserSummaryResponse user
) {
}
