package org.securehub.userservice.dto;

import jakarta.validation.constraints.Email;

public record UpdateUserRequest(

        @Email(message = "Invalid email")
        String email,

        String firstName,

        String lastName,

        Boolean isActive
) {
}
