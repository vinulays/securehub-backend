package org.securehub.organizationservice.repository;

import org.securehub.organizationservice.entity.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface OrganizationRepository extends JpaRepository<Organization, UUID>,
        JpaSpecificationExecutor<Organization> {

    Boolean existsBySlug(String slug);

    @Query("""
                    SELECT o
                    FROM Organization o
                    JOIN OrganizationMembership m
                        ON m.organization.id = o.id
                    WHERE m.userId = :userId
                        AND m.isActive = true
                        AND o.status = OrganizationStatus.ACTIVE
                    ORDER BY o.name
            """)
    List<Organization> findOrganizationByUserId(UUID userId);
}
