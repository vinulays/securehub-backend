package org.securehub.organizationservice.service;

import lombok.RequiredArgsConstructor;
import org.securehub.organizationservice.dto.CreateMembershipRequest;
import org.securehub.organizationservice.dto.MembershipResponse;
import org.securehub.organizationservice.dto.UserSummaryResponse;
import org.securehub.organizationservice.entity.Organization;
import org.securehub.organizationservice.entity.OrganizationMembership;
import org.securehub.organizationservice.exception.MembershipAlreadyExistsException;
import org.securehub.organizationservice.exception.MembershipNotFoundException;
import org.securehub.organizationservice.exception.OrganizationNotFoundException;
import org.securehub.organizationservice.exception.UserNotFoundException;
import org.securehub.organizationservice.repository.MembershipRepository;
import org.securehub.organizationservice.repository.OrganizationRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MembershipService {

    private final MembershipRepository membershipRepository;
    private final OrganizationRepository organizationRepository;
    private final UserLookupService userLookupService;

    public MembershipResponse addMember(UUID organizationId, CreateMembershipRequest request) {

        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new OrganizationNotFoundException("Organization not found"));

        UserSummaryResponse user = userLookupService.getUser(request.userId());

        if (user == null || user.isActive() == false) {
            throw new UserNotFoundException("User is not active or does not exist");
        }

        boolean membershipExists = membershipRepository
                .existsByOrganizationIdAndUserId(organizationId, request.userId());

        if (membershipExists) {
            throw new MembershipAlreadyExistsException("User is already a member");
        }

        OrganizationMembership membership = OrganizationMembership.builder()
                .organization(organization)
                .userId(request.userId())
                .role(request.role())
                .isActive(true)
                .build();

        membership = membershipRepository.save(membership);

        return mapToResponse(membership);
    }

    public void removeMember(UUID organizationId, UUID membershipId) {

        OrganizationMembership membership =
                membershipRepository.findByIdAndOrganizationId(membershipId, organizationId)
                        .orElseThrow(() -> new MembershipNotFoundException("Membership not found"));

        membershipRepository.delete(membership);
    }

    private MembershipResponse mapToResponse(OrganizationMembership membership) {

        return new MembershipResponse(
                membership.getId(),
                membership.getOrganization().getId(),
                membership.getUserId(),
                membership.getRole(),
                membership.getIsActive()
        );
    }
}
