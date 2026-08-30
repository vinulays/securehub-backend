package org.securehub.userservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AcceptInvitationRequest(
        @NotBlank(message = "Invitation token is required")
        String token,

        @NotBlank(message = "Password is required")
        @Size(min = 8, max = 12, message = "Password must be between 08 and 12 characters")
        @Pattern(
                regexp = "^(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).+$",
                message = "Password must contain at least one uppercase letter, one number, and one special character"
        )
        String password
) {
}
