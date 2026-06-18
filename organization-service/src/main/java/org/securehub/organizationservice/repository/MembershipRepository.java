package org.securehub.organizationservice.repository;

import org.securehub.organizationservice.entity.OrganizationMembership;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MembershipRepository extends
        JpaRepository<OrganizationMembership, UUID>,
        JpaSpecificationExecutor<OrganizationMembership> {

    Boolean existsByOrganizationIdAndUserId(UUID organizationId, UUID userId);

    Boolean existsByOrganizationIdAndUserIdAndIsActiveTrue(UUID organization, UUID userId);

    Optional<OrganizationMembership> findByOrganizationIdAndUserId(UUID organizationId, UUID uerId);

    List<OrganizationMembership> findByOrganizationId(UUID organizationId);

    @Query("""
                SELECT m.organization.id
                FROM OrganizationMembership m
                WHERE m.userId = :userId
                AND m.isActive = true
            """)
    List<UUID> findOrganizationIdsByUserIdAndIsActiveTrue(UUID userId);

    Optional<OrganizationMembership> findByIdAndOrganizationId(UUID id, UUID organizationId);

}
