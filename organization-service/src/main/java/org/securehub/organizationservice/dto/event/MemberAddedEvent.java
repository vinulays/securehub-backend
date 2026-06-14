package org.securehub.organizationservice.dto.event;

import org.securehub.organizationservice.enums.OrganizationRole;

import java.util.UUID;

public record MemberAddedEvent(

        UUID userId,
        String organizationName,
        String email,
        String firstName,
        OrganizationRole role
) {
}
