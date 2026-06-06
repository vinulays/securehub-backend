package org.securehub.userservice.security.model;

import java.util.List;
import java.util.UUID;

public record AuthenticatedUser(
        UUID userId,
        String keycloakUserId,
        String email,
        String firstName,
        String lastName,
        List<String> roles
) {
}
