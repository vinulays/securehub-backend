package org.securehub.organizationservice.service;

import lombok.RequiredArgsConstructor;
import org.securehub.organizationservice.dto.*;
import org.securehub.organizationservice.entity.Organization;
import org.securehub.organizationservice.entity.OrganizationMembership;
import org.securehub.organizationservice.exception.OrganizationNotFoundException;
import org.securehub.organizationservice.repository.MembershipRepository;
import org.securehub.organizationservice.repository.OrganizationRepository;
import org.securehub.organizationservice.specification.OrganizationSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrganizationService {

    private final OrganizationRepository organizationRepository;
    private final MembershipRepository membershipRepository;
    private final UserLookupService userLookupService;

    public OrganizationSummaryResponse createOrganization(CreateOrganizationRequest request) {

        Organization organization = Organization.builder()
                .name(request.name())
                .slug(request.slug())
                .description(request.description())
                .isActive(true)
                .build();

        return mapToOrganizationSummary(organizationRepository.save(organization));
    }

    public Page<OrganizationSummaryResponse> searchOrganizations(OrganizationSearchRequest request) {
        Specification<Organization> specification = OrganizationSpecification.search(request);

        String sortBy = ALLOWED_SORT_FIELDS.contains(request.sortBy()) ? request.sortBy() : "createdAt";

        Sort sort = Sort.by(
                request.sortDirection(),
                sortBy
        );

        Pageable pageable = PageRequest.of(
                request.page(),
                request.size(),
                sort
        );

        return organizationRepository
                .findAll(specification, pageable)
                .map(this::mapToOrganizationSummary);
    }

    public OrganizationSummaryResponse updateOrganization(UUID id, UpdateOrganizationRequest request) {

        Organization organization = organizationRepository.findById(id)
                .orElseThrow(() -> new OrganizationNotFoundException("Organization not found"));

        if (request.name() != null) {
            organization.setName(request.name());
        }

        if (request.description() != null) {
            organization.setDescription(request.description());
        }

        return mapToOrganizationSummary(organizationRepository.save(organization));
    }

    public OrganizationDetailsResponse getOrganizationDetails(UUID organizationId) {
        Organization organization = organizationRepository
                .findById(organizationId)
                .orElseThrow(() -> new OrganizationNotFoundException("Organization not found"));

        List<OrganizationMembership> memberships = membershipRepository.findByOrganizationId(organizationId);

        List<MembershipDetailsResponse> memberResponses = memberships.stream()
                .map(this::mapToMembershipDetails)
                .toList();

        return new OrganizationDetailsResponse(
                organization.getId(),
                organization.getName(),
                organization.getSlug(),
                organization.getDescription(),
                organization.getIsActive(),
                memberResponses
        );
    }

    public void updateOrganizationStatus(UUID organizationId, boolean active) {
        Organization organization = organizationRepository
                .findById(organizationId)
                .orElseThrow(() -> new OrganizationNotFoundException("Organization not found"));

        organization.setIsActive(active);

        organizationRepository.save(organization);
    }

    private OrganizationSummaryResponse mapToOrganizationSummary(Organization org) {
        return new OrganizationSummaryResponse(
                org.getId(),
                org.getName(),
                org.getSlug(),
                org.getDescription(),
                org.getIsActive()
        );
    }

    private MembershipDetailsResponse mapToMembershipDetails(OrganizationMembership membership) {

        UserSummaryResponse user = userLookupService.getUser(membership.getUserId());

        return new MembershipDetailsResponse(
                membership.getId(),
                membership.getUserId(),
                membership.getRole(),
                membership.getIsActive(),
                user
        );
    }

    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
            "name",
            "createdAt"
    );
}
