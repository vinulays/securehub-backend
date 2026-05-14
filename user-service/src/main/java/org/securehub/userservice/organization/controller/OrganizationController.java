package org.securehub.userservice.organization.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.securehub.userservice.organization.dto.CreateOrganizationRequest;
import org.securehub.userservice.organization.dto.OrganizationResponse;
import org.securehub.userservice.organization.service.OrganizationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/organizations")
@RequiredArgsConstructor
public class OrganizationController {

    private final OrganizationService organizationService;

    @PostMapping
    public OrganizationResponse createOrganization(@Valid @RequestBody CreateOrganizationRequest request) {
        return organizationService.createOrganization(request);
    }

    @GetMapping("/me")
    public List<OrganizationResponse> getMyOrganizations() {
        return organizationService.getMyOrganizations();
    }
}
