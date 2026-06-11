package org.securehub.organizationservice.service;

import lombok.RequiredArgsConstructor;
import org.securehub.organizationservice.client.UserClient;
import org.securehub.organizationservice.dto.UserSummaryResponse;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserLookupService {

    private final UserClient userClient;

    public UserSummaryResponse getUser(UUID userId) {
        return userClient.getUser(userId);
    }
}
