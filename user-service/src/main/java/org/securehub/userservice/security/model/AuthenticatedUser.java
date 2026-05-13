package org.securehub.userservice.security.model;

import java.util.List;
import java.util.UUID;

public record AuthenticatedUser(
        UUID userId,
        String keycloakUserId,
        String email,
        List<String> roles
) {
}
