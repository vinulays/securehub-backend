package org.securehub.organizationservice.dto;

import jakarta.validation.constraints.NotNull;
import org.securehub.organizationservice.enums.OrganizationRole;

import java.util.UUID;

public record CreateMembershipRequest(

        @NotNull(message = "User ID is required")
        UUID userId,

        @NotNull(message = "Organization role is required")
        OrganizationRole role
) {
}
