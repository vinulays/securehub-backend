package org.securehub.userservice.dto;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public record AuthenticatedUserResponse(
        UUID userId,
        String keycloakUserId,
        String email,
        String firstName,
        String lastName,
        Set<String> permissions,
        List<String> roles
) {
}
