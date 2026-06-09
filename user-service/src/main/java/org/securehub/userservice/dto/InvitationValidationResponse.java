package org.securehub.userservice.dto;

public record InvitationValidationResponse(
        boolean valid,

        String firstName,

        String lastName,

        String email

) {
}
