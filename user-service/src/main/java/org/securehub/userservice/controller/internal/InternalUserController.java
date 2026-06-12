package org.securehub.userservice.controller.internal;

import lombok.RequiredArgsConstructor;
import org.securehub.userservice.dto.*;
import org.securehub.userservice.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/internal/users")
@RequiredArgsConstructor
public class InternalUserController {

    private final UserService userService;

    @PostMapping("/batch")
    public ResponseEntity<UserBatchResponse> getUsersBatch(@RequestBody UserBatchRequest request){

        return ResponseEntity.ok(userService.getUsersByIds(request.userIds()));
    }

    @PostMapping("/search-ids")
    public UserIdsResponse searchUserIds(
            @RequestBody UserSearchIdsRequest request
    ) {
        return userService.searchUserIds(request);
    }

    @GetMapping("/keycloak/{keycloakUserId}")
    public ResponseEntity<UserSummaryResponse> getByKeycloakId(@PathVariable String keycloakUserId){

        return ResponseEntity.ok(userService.getUserSummaryByKeycloakId(keycloakUserId));

    }

    @GetMapping("/{id}")
    public ResponseEntity<UserSummaryResponse> getUserSummary(@PathVariable UUID id) {

        return ResponseEntity.ok(userService.getUserSummary(id));
    }
}