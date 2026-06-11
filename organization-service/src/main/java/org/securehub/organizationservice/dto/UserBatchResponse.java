package org.securehub.organizationservice.dto;

import java.util.List;

public record UserBatchResponse(

        List<UserSummaryResponse> users
) {
}
