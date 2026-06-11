package org.securehub.userservice.repository;

import org.securehub.userservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID>, JpaSpecificationExecutor<User> {

    Optional<User> findByKeycloakUserId(String keycloakUserId);

    Optional<User> findByEmail(String email);

    Boolean existsByEmail(String email);

    @Query("""
            SELECT u.id
            FROM User u
            WHERE
                LOWER(u.firstName) LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(CONCAT(u.firstName, ' ', u.lastName))
                    LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%'))
            """)
    List<UUID> searchUserIds(String keyword);
}
