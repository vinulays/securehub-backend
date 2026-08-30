package org.securehub.organizationservice.dto;

import org.securehub.organizationservice.enums.OrganizationStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record OrganizationDetailsResponse(
        UUID id,
        String name,
        String slug,
        String description,
        OrganizationStatus status,
        LocalDateTime createdAt,
        List<MembershipDetailsResponse> members
) {
}
