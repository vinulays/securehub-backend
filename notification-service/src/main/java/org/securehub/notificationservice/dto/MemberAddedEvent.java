package org.securehub.notificationservice.dto;

import java.util.UUID;

public record MemberAddedEvent(

        UUID userId,
        String organizationName,
        String email,
        String firstName,
        String role
) {
}
