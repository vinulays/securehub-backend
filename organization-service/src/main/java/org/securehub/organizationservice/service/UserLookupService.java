package org.securehub.organizationservice.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.securehub.organizationservice.client.UserClient;
import org.securehub.organizationservice.dto.*;
import org.securehub.organizationservice.exception.UserNotFoundException;
import org.securehub.organizationservice.exception.UserServiceUnavailableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserLookupService {

    private final UserClient userClient;

    @Retry(name = "user-service")
    @CircuitBreaker(name = "user-service", fallbackMethod = "userServiceFallback")
    public Map<UUID, UserSummaryResponse> getUsers(List<UUID> userIds) {

        UserBatchResponse response = userClient.getUsersBatch(
                new UserBatchRequest(userIds)
        );

        return response.users().stream()
                .collect(Collectors.toMap(UserSummaryResponse::id, user -> user));
    }

    @Retry(name = "user-service")
    @CircuitBreaker(name = "user-service", fallbackMethod = "userServiceFallback")
    public UserIdsResponse searchUserIds(UserSearchIdsRequest request) {
        return userClient.searchUserIds(request);
    }


    @Retry(name = "user-service")
    @CircuitBreaker(name = "user-service", fallbackMethod = "userServiceFallback")
    public UserSummaryResponse getUserByKeycloakUserId(String keycloakUserId) {
        return userClient.getUserByKeycloakUserId(keycloakUserId);
    }

    @Retry(name = "user-service")
    @CircuitBreaker(name = "user-service", fallbackMethod = "userServiceFallback")
    public UserSummaryResponse getUser(UUID userId) {
        return userClient.getUser(userId);
    }

    private UserSummaryResponse userServiceFallback(UUID userId, Throwable ex) {
        return fallback("getUser", ex);
    }

    private UserSummaryResponse userServiceFallback(String keycloakUserId, Throwable ex) {
        return fallback("getUserByKeycloakUserId", ex);
    }

    private Map<UUID, UserSummaryResponse> userServiceFallback(List<UUID> userIds, Throwable ex) {
        return fallback("getUsers", ex);
    }

    private UserIdsResponse userServiceFallback(UserSearchIdsRequest request, Throwable ex) {
        return fallback("searchUserIds", ex);
    }

    private <T> T fallback(String operation, Throwable ex) {

        log.warn("User service unavailable during {}", operation);

        throw handleFallback(ex);
    }

    private RuntimeException handleFallback(Throwable ex) {

        if (ex instanceof UserNotFoundException ||
                ex instanceof AccessDeniedException ||
                ex instanceof IllegalArgumentException) {

            return (RuntimeException) ex;
        }

        return new UserServiceUnavailableException("User service unavailable");
    }
}
