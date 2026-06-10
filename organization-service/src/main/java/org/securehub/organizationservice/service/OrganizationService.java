package org.securehub.organizationservice.service;

import lombok.RequiredArgsConstructor;
import org.securehub.organizationservice.dto.CreateOrganizationRequest;
import org.securehub.organizationservice.dto.OrganizationResponse;
import org.securehub.organizationservice.dto.OrganizationSearchRequest;
import org.securehub.organizationservice.dto.UpdateOrganizationRequest;
import org.securehub.organizationservice.entity.Organization;
import org.securehub.organizationservice.exception.OrganizationNotFoundException;
import org.securehub.organizationservice.repository.OrganizationRepository;
import org.securehub.organizationservice.specification.OrganizationSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrganizationService {

    private final OrganizationRepository organizationRepository;

    public OrganizationResponse createOrganization(CreateOrganizationRequest request) {

        Organization organization = Organization.builder()
                .name(request.name())
                .slug(request.slug())
                .description(request.description())
                .isActive(true)
                .build();

        return toResponse(organizationRepository.save(organization));
    }

    public Page<OrganizationResponse> searchOrganizations(OrganizationSearchRequest request) {
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
                .map(this::toResponse);
    }

    public OrganizationResponse updateOrganization(UUID id, UpdateOrganizationRequest request) {

        Organization organization = organizationRepository.findById(id)
                .orElseThrow(() -> new OrganizationNotFoundException("Organization not found"));

        if (request.name() != null) {
            organization.setName(request.name());
        }

        if (request.description() != null) {
            organization.setDescription(request.description());
        }

        return toResponse(organizationRepository.save(organization));
    }

    public OrganizationResponse getOrganizationDetails(UUID organizationId) {
        Organization organization = organizationRepository
                .findById(organizationId)
                .orElseThrow(() -> new OrganizationNotFoundException("Organization not found"));

        return toResponse(organization);
    }

    public void updateOrganizationStatus(UUID organizationId, boolean active) {
        Organization organization = organizationRepository
                .findById(organizationId)
                .orElseThrow(() -> new OrganizationNotFoundException("Organization not found"));

        organization.setIsActive(active);

        organizationRepository.save(organization);
    }

    private OrganizationResponse toResponse(Organization org) {
        return new OrganizationResponse(
                org.getId(),
                org.getName(),
                org.getSlug(),
                org.getDescription(),
                org.getIsActive()
        );
    }

    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
            "name",
            "createdAt"
    );
}
