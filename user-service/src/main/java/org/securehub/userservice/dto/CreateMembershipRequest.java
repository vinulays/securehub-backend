package org.securehub.userservice.dto;

import org.securehub.userservice.enums.OrganizationRole;

import java.util.UUID;

public record CreateMembershipRequest(
        UUID userId,
        OrganizationRole role,
        String email,
        String firstName
) {
}
