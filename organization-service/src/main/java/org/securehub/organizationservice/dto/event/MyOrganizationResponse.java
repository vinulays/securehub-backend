package org.securehub.organizationservice.dto.event;

import java.util.UUID;

public record MyOrganizationResponse(

        UUID id,

        String name,

        String slug
) {
}
