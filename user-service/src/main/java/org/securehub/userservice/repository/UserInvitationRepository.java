package org.securehub.userservice.repository;

import org.securehub.userservice.entity.UserInvitation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface UserInvitationRepository extends JpaRepository<UserInvitation, UUID> {

    Optional<UserInvitation> findByToken(String token);

    @Modifying
    @Query("""
        update UserInvitation ui
        set ui.used = true
        where ui.user.id = :userId
        and ui.used = false
    """)
    void invalidateUserInvitations(@Param("userId") UUID userId);
}
