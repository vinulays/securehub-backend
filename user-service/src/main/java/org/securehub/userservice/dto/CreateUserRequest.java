package org.securehub.userservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.UUID;
import org.securehub.userservice.enums.OrganizationRole;
import org.securehub.userservice.enums.UserRole;

public record CreateUserRequest(

        @Email(message = "Invalid email format")
        @NotBlank(message = "Email is required")
        String email,

        @NotBlank(message = "First name is required")
        String firstName,

        @NotBlank(message = "Last name is required")
        String lastName,

        @NotNull(message = "Organization ID is required")
        @UUID(message = "Organization ID must be a valid UUID")
        String organizationId,

        @NotNull(message = "Organization role is required")
        OrganizationRole organizationRole,

        @NotNull(message = "Role is required")
        UserRole role
) {
}
