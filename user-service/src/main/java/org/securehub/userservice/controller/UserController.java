package org.securehub.userservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.securehub.userservice.dto.UserResponse;
import org.securehub.userservice.dto.UserSearchRequest;
import org.securehub.userservice.model.AuthenticatedUser;
import org.securehub.userservice.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @PostMapping("/search")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UserResponse>> getUsers(@Valid @RequestBody UserSearchRequest request) {

        return ResponseEntity.ok(userService.searchUsers(request));
    }

    @GetMapping("/me")
    public AuthenticatedUser getCurrentUser() {

        return userService.getCurrentUser();
    }
}
