package org.securehub.organizationservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.securehub.organizationservice.dto.*;
import org.securehub.organizationservice.service.MembershipService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/organizations/{organizationId}/members")
@RequiredArgsConstructor
public class MembershipController {

    private final MembershipService membershipService;

    @PostMapping("/search")
    @PreAuthorize(
            "@orgAuth.hasPermission(" +
                    "#organizationId, " +
                    "T(org.securehub.organizationservice.enums.OrganizationPermission).MEMBER_VIEW)"
    )
    public ResponseEntity<Page<MembershipDetailsResponse>> searchMembers(
            @PathVariable UUID organizationId,
            @Valid @RequestBody MembershipSearchRequest request
    ) {

        return ResponseEntity.ok(membershipService.getMembers(organizationId, request));
    }

    @PostMapping
    @PreAuthorize(
            "@orgAuth.hasPermission(" +
                    "#organizationId, " +
                    "T(org.securehub.organizationservice.enums.OrganizationPermission).MEMBER_CREATE)"
    )
    public ResponseEntity<MembershipResponse> addMember(
            @PathVariable UUID organizationId,
            @Valid @RequestBody CreateMembershipRequest request
    ) {

        return ResponseEntity.ok(
                membershipService.addMember(organizationId, request)
        );
    }


    @PatchMapping("/{membershipId}/role")
    @PreAuthorize(
            "@orgAuth.hasPermission(" +
                    "#organizationId, " +
                    "T(org.securehub.organizationservice.enums.OrganizationPermission).MEMBER_UPDATE)"
    )
    public ResponseEntity<Void> updateMembershipRole(
            @PathVariable UUID organizationId,
            @PathVariable UUID membershipId,
            @Valid @RequestBody UpdateMembershipRoleRequest request
    ) {

        membershipService.updateMembershipRole(organizationId, membershipId, request);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{membershipId}")
    @PreAuthorize(
            "@orgAuth.hasPermission(" +
                    "#organizationId, " +
                    "T(org.securehub.organizationservice.enums.OrganizationPermission).MEMBER_DELETE)"
    )
    public ResponseEntity<Void> removeMember(
            @PathVariable UUID organizationId,
            @PathVariable UUID membershipId
    ) {

        membershipService.removeMember(organizationId, membershipId);

        return ResponseEntity.noContent().build();
    }
}
