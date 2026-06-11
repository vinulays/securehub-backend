package org.securehub.organizationservice.dto;

import java.util.List;
import java.util.UUID;

public record OrganizationDetailsResponse(
        UUID id,
        String name,
        String slug,
        String description,
        boolean isActive,
        List<MembershipDetailsResponse> members
) {
}
