package org.securehub.userservice.dto;

import java.util.UUID;

public record UserSummaryResponse(

        UUID id,

        String email,

        String firstName,

        String lastName,

        Boolean isActive

) {
}