package org.securehub.organizationservice.service;

import lombok.RequiredArgsConstructor;
import org.securehub.organizationservice.client.UserClient;
import org.securehub.organizationservice.dto.UserBatchRequest;
import org.securehub.organizationservice.dto.UserBatchResponse;
import org.securehub.organizationservice.dto.UserSummaryResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserLookupService {

    private final UserClient userClient;

    public Map<UUID, UserSummaryResponse> getUsers(List<UUID> userIds) {

        UserBatchResponse response = userClient.getUsersBatch(
                new UserBatchRequest(userIds)
        );

        return response.users().stream()
                .collect(Collectors.toMap(UserSummaryResponse::id, user -> user));
    }

    public UserSummaryResponse getUser(UUID userId) {
        return userClient.getUser(userId);
    }
}
