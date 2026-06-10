package org.securehub.userservice.event;

import java.util.UUID;

public record UserCreatedDomainEvent(
        UUID userId
) {
}
