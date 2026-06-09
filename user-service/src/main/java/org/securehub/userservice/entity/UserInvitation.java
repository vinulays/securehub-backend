package org.securehub.userservice.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "user_invitations")
public class UserInvitation {

    @Id
    @GeneratedValue
    private UUID id;

    private String token;

    private UUID userId;

    private LocalDateTime expiresAt;

    private LocalDateTime usedAt;

    private boolean used;
}
