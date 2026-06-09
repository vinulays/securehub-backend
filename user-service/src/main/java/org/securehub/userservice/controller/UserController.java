package org.securehub.userservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.securehub.userservice.dto.*;
import org.securehub.userservice.model.AuthenticatedUser;
import org.securehub.userservice.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @PostMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse createUser(@Valid @RequestBody CreateUserRequest request) {

        return userService.createUser(request);
    }

    @PostMapping("/search")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UserResponse>> getUsers(@Valid @RequestBody UserSearchRequest request) {

        return ResponseEntity.ok(userService.searchUsers(request));
    }

    @PostMapping("/invitations/accept")
    public ResponseEntity<Void> acceptInvitation(@Valid @RequestBody AcceptInvitationRequest request) {
        userService.acceptInvitation(request);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/invitations/{token}")
    public ResponseEntity<InvitationValidationResponse> validateInvitation(@PathVariable String token) {

        return ResponseEntity.ok(userService.validateInvitation(token));
    }

    @GetMapping("/me")
    public AuthenticatedUser getCurrentUser() {

        return userService.getCurrentUser();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse updateUser(@PathVariable UUID id, @Valid @RequestBody UpdateUserRequest request) {

        return userService.updateUser(id, request);
    }
}
