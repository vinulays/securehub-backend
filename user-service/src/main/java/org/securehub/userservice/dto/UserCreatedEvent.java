package org.securehub.userservice.dto;

import java.util.UUID;

public record UserCreatedEvent(
        UUID userId,
        String email,
        String firstName,
        String lastName,
        String token
) {
}
