package org.securehub.userservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.securehub.userservice.dto.*;
import org.securehub.userservice.dto.AuthenticatedUserResponse;
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

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/resend-invitation")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> resendInvitation(@PathVariable UUID id) {
        userService.resendInvitation(id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/invitations/{token}")
    public ResponseEntity<InvitationValidationResponse> validateInvitation(@PathVariable String token) {

        return ResponseEntity.ok(userService.validateInvitation(token));
    }

    @GetMapping("/me")
    public AuthenticatedUserResponse getCurrentUser() {

        return userService.getCurrentUser();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> getUserDetails(@PathVariable UUID id) {

        return ResponseEntity.ok(userService.getUserDetails(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse updateUser(@PathVariable UUID id, @Valid @RequestBody UpdateUserRequest request) {

        return userService.updateUser(id, request);
    }

    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse deactivateUser(@PathVariable UUID id) {

        return userService.updateUserStatus(id, false);
    }

    @PatchMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse activateUser(@PathVariable UUID id) {

        return userService.updateUserStatus(id, true);
    }
}
