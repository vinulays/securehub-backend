package org.securehub.userservice.controller.internal;

import lombok.RequiredArgsConstructor;
import org.securehub.userservice.dto.UserSummaryResponse;
import org.securehub.userservice.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/internal/users")
@RequiredArgsConstructor
public class InternalUserController {

    private final UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<UserSummaryResponse> getUserSummary(@PathVariable UUID id) {

        return ResponseEntity.ok(userService.getUserSummary(id));
    }
}