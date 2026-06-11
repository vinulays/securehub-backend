package org.securehub.userservice.dto;

import java.util.List;
import java.util.UUID;

public record UserBatchRequest(

        List<UUID> userIds
) {
}
