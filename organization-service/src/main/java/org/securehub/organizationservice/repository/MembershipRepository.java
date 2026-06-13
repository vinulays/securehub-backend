package org.securehub.organizationservice.repository;

import org.securehub.organizationservice.entity.OrganizationMembership;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MembershipRepository extends
        JpaRepository<OrganizationMembership, UUID>,
        JpaSpecificationExecutor<OrganizationMembership> {

    Boolean existsByOrganizationIdAndUserId(UUID organizationId, UUID userId);

    Optional<OrganizationMembership> findByOrganizationIdAndUserId(UUID organizationId, UUID uerId);

    List<OrganizationMembership> findByOrganizationId(UUID organizationId);

    Optional<OrganizationMembership> findByIdAndOrganizationId(UUID id, UUID organizationId);

}
