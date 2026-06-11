package org.securehub.organizationservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.securehub.organizationservice.dto.CreateMembershipRequest;
import org.securehub.organizationservice.dto.MembershipDetailsResponse;
import org.securehub.organizationservice.dto.MembershipResponse;
import org.securehub.organizationservice.dto.MembershipSearchRequest;
import org.securehub.organizationservice.service.MembershipService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/organizations/{organizationId}/members")
@RequiredArgsConstructor
public class MembershipController {

    private final MembershipService membershipService;

    @PostMapping("/search")
    public ResponseEntity<Page<MembershipDetailsResponse>> searchMembers(
            @PathVariable UUID organizationId,
            @Valid @RequestBody MembershipSearchRequest request
    ) {

        return ResponseEntity.ok(membershipService.getMembers(organizationId, request));
    }

    @PostMapping
    public ResponseEntity<MembershipResponse> addMember(
            @PathVariable UUID organizationId,
            @Valid @RequestBody CreateMembershipRequest request
    ) {

        return ResponseEntity.ok(
                membershipService.addMember(organizationId, request)
        );
    }

    @DeleteMapping("/{membershipId}")
    public ResponseEntity<Void> removeMember(
            @PathVariable UUID organizationId,
            @PathVariable UUID membershipId
    ) {

        membershipService.removeMember(organizationId, membershipId);

        return ResponseEntity.noContent().build();
    }
}
