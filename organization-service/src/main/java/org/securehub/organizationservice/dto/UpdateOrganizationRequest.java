package org.securehub.organizationservice.dto;

import jakarta.validation.constraints.NotEmpty;
import org.hibernate.validator.constraints.Length;

public record UpdateOrganizationRequest(

        @NotEmpty(message = "Name is required")
        String name,

        @Length(max = 2000, message = "Description must not exceed 2000 characters")
        String description
) {
}
