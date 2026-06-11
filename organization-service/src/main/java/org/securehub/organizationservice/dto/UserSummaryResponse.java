package org.securehub.organizationservice.dto;

import java.util.UUID;

public record UserSummaryResponse(

        UUID id,

        String email,

        String firstName,

        String lastName,

        Boolean isActive

) {
}