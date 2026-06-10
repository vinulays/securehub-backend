package org.securehub.userservice.dto.event;

import java.util.UUID;

public record UserInvitationCreatedEvent(
        UUID userId,
        String email,
        String firstName,
        String lastName,
        String token
) {
}
