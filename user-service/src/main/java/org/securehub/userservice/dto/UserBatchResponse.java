package org.securehub.userservice.dto;

import java.util.List;

public record UserBatchResponse(

        List<UserSummaryResponse> users
) {
}
