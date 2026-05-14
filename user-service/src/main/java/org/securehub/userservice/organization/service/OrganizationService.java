package org.securehub.userservice.organization.service;

import lombok.RequiredArgsConstructor;
import org.securehub.userservice.membership.entity.OrganizationMembership;
import org.securehub.userservice.membership.repository.OrganizationMembershipRepository;
import org.securehub.userservice.organization.dto.CreateOrganizationRequest;
import org.securehub.userservice.organization.dto.OrganizationResponse;
import org.securehub.userservice.organization.entity.Organization;
import org.securehub.userservice.organization.repository.OrganizationRepository;
import org.securehub.userservice.role.OrganizationRole;
import org.securehub.userservice.security.service.CurrentUserService;
import org.securehub.userservice.user.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrganizationService {

    private final OrganizationRepository organizationRepository;
    private final OrganizationMembershipRepository membershipRepository;
    private final CurrentUserService currentUserService;

    @Transactional
    public OrganizationResponse createOrganization(
            CreateOrganizationRequest request
    ) {

        User currentUser = currentUserService.getCurrentDatabaseUser();

        Organization organization = Organization.builder()
                .name(request.name())
                .slug(generateSlug(request.name()))
                .description(request.description())
                .build();

        organization = organizationRepository.save(organization);

        OrganizationMembership membership =
                OrganizationMembership.builder()
                        .organization(organization)
                        .user(currentUser)
                        .role(OrganizationRole.ROLE_ORG_ADMIN)
                        .isActive(true)
                        .build();

        membershipRepository.save(membership);

        return mapToResponse(organization);
    }

    public List<OrganizationResponse> getMyOrganizations() {

        User currentUser = currentUserService.getCurrentDatabaseUser();

        return membershipRepository.findByUser(currentUser)
                .stream()
                .map(OrganizationMembership::getOrganization)
                .map(this::mapToResponse)
                .toList();
    }

    private OrganizationResponse mapToResponse(
            Organization organization
    ) {

        return new OrganizationResponse(
                organization.getId(),
                organization.getName(),
                organization.getSlug(),
                organization.getDescription()
        );
    }

    private String generateSlug(String name) {

        return name
                .toLowerCase()
                .replaceAll("[^a-z0-9\\s]", "")
                .replaceAll("\\s+", "-");
    }
}
