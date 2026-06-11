package org.securehub.organizationservice.dto;

import jakarta.validation.constraints.NotNull;
import org.securehub.organizationservice.enums.OrganizationRole;

public record UpdateMembershipRoleRequest(

        @NotNull(message = "Organization role is required")
        OrganizationRole role
) {
}
