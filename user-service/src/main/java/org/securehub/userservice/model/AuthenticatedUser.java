package org.securehub.userservice.model;

import java.util.Set;
import java.util.UUID;

public record AuthenticatedUser(
        UUID userId,
        String keycloakUserId,
        String email,
        String firstName,
        String lastName,
        Set<String> permissions
) {
}
