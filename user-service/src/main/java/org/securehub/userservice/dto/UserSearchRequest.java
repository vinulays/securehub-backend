package org.securehub.userservice.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.UUID;
import org.securehub.userservice.enums.SortDirection;

import java.util.List;

@Getter
@Setter
public class UserSearchRequest {

    private String keyword;

    private Boolean isActive;

    @Size(max = 20, message = "A maximum of 20 organization IDs can be provided")
    private List<@UUID(message = "Each organization ID must be a valid UUID") String> organizationIds;

    @Min(value = 0, message = "Page number cannot be negative")
    private int page = 0;

    @Min(value = 1, message = "Page size must be at least 1")
    @Max(value = 100, message = "Page size cannot exceed 100")
    private int size = 10;

    private String sortBy = "createdAt";

    private SortDirection sortDirection = SortDirection.DESC;
}