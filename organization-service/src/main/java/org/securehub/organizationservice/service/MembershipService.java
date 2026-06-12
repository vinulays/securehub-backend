package org.securehub.organizationservice.service;

import lombok.RequiredArgsConstructor;
import org.securehub.organizationservice.dto.*;
import org.securehub.organizationservice.entity.Organization;
import org.securehub.organizationservice.entity.OrganizationMembership;
import org.securehub.organizationservice.exception.MembershipAlreadyExistsException;
import org.securehub.organizationservice.exception.MembershipNotFoundException;
import org.securehub.organizationservice.exception.OrganizationNotFoundException;
import org.securehub.organizationservice.exception.UserNotFoundException;
import org.securehub.organizationservice.repository.MembershipRepository;
import org.securehub.organizationservice.repository.OrganizationRepository;
import org.securehub.organizationservice.specification.MembershipSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
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

    public Page<MembershipDetailsResponse> getMembers(UUID organizationId, MembershipSearchRequest request) {

        List<UUID> matchingUserIds = null;

        if (StringUtils.hasText(request.keyword())) {

            UserIdsResponse response = userLookupService.searchUserIds(new UserSearchIdsRequest(request.keyword()));

            matchingUserIds = response.userIds();
        }

        Specification<OrganizationMembership> specification =
                MembershipSpecification.search(organizationId, request, matchingUserIds);

        Pageable pageable = PageRequest.of(request.page(), request.size());

        if (matchingUserIds != null && matchingUserIds.isEmpty()) {
            return Page.empty(pageable);
        }

        Page<OrganizationMembership> memberships = membershipRepository.findAll(specification, pageable);

        List<UUID> userIds = memberships.getContent()
                .stream()
                .map(OrganizationMembership::getUserId)
                .toList();

        Map<UUID, UserSummaryResponse> users = userLookupService.getUsers(userIds);

        List<MembershipDetailsResponse> content =
                memberships.getContent()
                        .stream()
                        .map(membership -> {

                            UserSummaryResponse user = users.get(membership.getUserId());

                            return new MembershipDetailsResponse(
                                    membership.getId(),
                                    membership.getUserId(),
                                    membership.getRole(),
                                    membership.getIsActive(),
                                    user
                            );
                        })
                        .toList();

        return new PageImpl<>(
                content,
                pageable,
                memberships.getTotalElements()
        );
    }

    public void updateMembershipRole(UUID organizationId, UUID membershipId, UpdateMembershipRoleRequest request){

        OrganizationMembership membership =
                membershipRepository.findByIdAndOrganizationId(membershipId, organizationId)
                        .orElseThrow(() -> new MembershipNotFoundException("Membership not found"));

        if(membership.getRole() != request.role()){
            membership.setRole(request.role());

            membershipRepository.save(membership);
        }
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
