package org.securehub.organizationservice.dto;

import java.util.UUID;

public record OrganizationSummaryResponse(
        UUID id,
        String name,
        String slug,
        String description,
        Boolean isActive
) {
}
