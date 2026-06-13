package org.securehub.userservice.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.UUID;
import org.springframework.data.domain.Sort;

import java.util.List;

public record UserSearchRequest(
        String keyword,

        Boolean isActive,

        @Size(max = 20, message = "A maximum of 20 organization IDs can be provided")
        List<@UUID(message = "Each organization ID must be a valid UUID") String> organizationIds,

        @Min(value = 0, message = "Page number cannot be negative")
        Integer page,

        @Min(value = 1, message = "Page size must be at least 1")
        @Max(value = 100, message = "Page size cannot exceed 100")
        Integer size,

        String sortBy,

        Sort.Direction sortDirection
) {

    public UserSearchRequest {
        page = page == null ? 0 : page;
        size = size == null ? 10 : size;
        sortBy = sortBy == null || sortBy.isBlank() ? "createdAt" : sortBy;
        sortDirection = sortDirection == null ? Sort.Direction.DESC : sortDirection;
    }

}