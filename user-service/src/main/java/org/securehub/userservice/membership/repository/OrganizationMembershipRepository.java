package org.securehub.userservice.membership.repository;

import org.securehub.userservice.membership.entity.OrganizationMembership;
import org.securehub.userservice.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OrganizationMembershipRepository extends JpaRepository<OrganizationMembership, UUID> {

    List<OrganizationMembership> findByUser(User user);
}
