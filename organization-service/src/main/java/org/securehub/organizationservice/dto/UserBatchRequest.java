package org.securehub.organizationservice.dto;

import java.util.List;
import java.util.UUID;

public record UserBatchRequest(

        List<UUID> userIds
) {
}
