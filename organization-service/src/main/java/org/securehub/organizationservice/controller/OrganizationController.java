package org.securehub.organizationservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.securehub.organizationservice.dto.*;
import org.securehub.organizationservice.service.OrganizationService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/organizations")
@RequiredArgsConstructor
public class OrganizationController {

    private final OrganizationService organizationService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrganizationSummaryResponse> createOrganization(
            @Valid @RequestBody CreateOrganizationRequest request
    ) {
        return ResponseEntity.ok(organizationService.createOrganization(request));
    }

    @PostMapping("/search")
    public ResponseEntity<Page<OrganizationSummaryResponse>> searchOrganizations(
            @Valid @RequestBody OrganizationSearchRequest request
    ) {

        return ResponseEntity.ok(
                organizationService.searchOrganizations(request)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrganizationDetailsResponse> getOrganizationDetails(@PathVariable UUID id) {
        return ResponseEntity.ok(organizationService.getOrganizationDetails(id));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrganizationSummaryResponse> updateOrganization(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateOrganizationRequest request
    ) {
        return ResponseEntity.ok(organizationService.updateOrganization(id, request));
    }

    @PatchMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> activateOrganization(@PathVariable UUID id) {
        organizationService.updateOrganizationStatus(id, true);

        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deactivateOrganization(@PathVariable UUID id) {
        organizationService.updateOrganizationStatus(id, false);

        return ResponseEntity.ok().build();
    }

}
