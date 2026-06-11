package org.securehub.organizationservice.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.securehub.organizationservice.enums.OrganizationRole;

public record MembershipSearchRequest(

        String keyword,

        OrganizationRole role,

        Boolean isActive,

        @Min(value = 0, message = "Page number cannot be negative")
        Integer page,

        @Min(value = 1, message = "Page size must be at least 1")
        @Max(value = 100, message = "Page size cannot exceed 100")
        Integer size
) {

    public MembershipSearchRequest {
        page = page == null ? 0 : page;
        size = size == null ? 10 : size;
    }
}
