package org.securehub.userservice.controller.internal;

import lombok.RequiredArgsConstructor;
import org.securehub.userservice.dto.UserBatchRequest;
import org.securehub.userservice.dto.UserBatchResponse;
import org.securehub.userservice.dto.UserSummaryResponse;
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

    @GetMapping("/{id}")
    public ResponseEntity<UserSummaryResponse> getUserSummary(@PathVariable UUID id) {

        return ResponseEntity.ok(userService.getUserSummary(id));
    }
}