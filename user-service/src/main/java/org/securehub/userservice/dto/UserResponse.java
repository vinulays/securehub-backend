package org.securehub.userservice.dto;

import org.securehub.userservice.entity.User;

import java.util.UUID;

public record UserResponse(
        UUID id,
        String email,
        String firstName,
        String lastName,
        Boolean isActive
) {
    public static UserResponse fromEntity(User user) {
        return new UserResponse(user.getId(), user.getEmail(), user.getFirstName(), user.getLastName(), user.getIsActive());
    }
}
