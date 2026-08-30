package org.securehub.organizationservice.dto;

import org.securehub.organizationservice.enums.OrganizationStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record OrganizationSummaryResponse(
        UUID id,
        String name,
        String slug,
        String description,
        OrganizationStatus status,
        LocalDateTime createdAt
) {
}
