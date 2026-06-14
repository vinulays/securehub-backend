package org.securehub.userservice.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.securehub.userservice.dto.*;
import org.securehub.userservice.entity.User;
import org.securehub.userservice.entity.UserInvitation;
import org.securehub.userservice.enums.UserRolePermissionMapping;
import org.securehub.userservice.exception.InvalidInvitationException;
import org.securehub.userservice.exception.UserAlreadyExistsException;
import org.securehub.userservice.exception.UserNotFoundException;
import org.securehub.userservice.repository.UserInvitationRepository;
import org.securehub.userservice.repository.UserRepository;
import org.securehub.userservice.specification.UserSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserInvitationRepository userInvitationRepository;
    private final InvitationService invitationService;
    private final KeycloakAdminService keycloakAdminService;

    @Transactional
    public UserResponse createUser(CreateUserRequest request) {

        boolean exists = userRepository.existsByEmail(request.email());

        if (exists) {
            throw new UserAlreadyExistsException("User already exists with email");
        }

        String keycloakUserId = keycloakAdminService.createUser(
                request.email(),
                request.firstName(),
                request.lastName(),
                request.role()
        );

        User user = new User();

        user.setKeycloakUserId(keycloakUserId);
        user.setEmail(request.email());
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setIsActive(false);

        User savedUser = userRepository.save(user);

        invitationService.createAndSendInvitation(savedUser);

        return UserResponse.fromEntity(savedUser);
    }

    @Transactional
    public void acceptInvitation(AcceptInvitationRequest request) {
        UserInvitation invitation =
                userInvitationRepository.findByToken(request.token())
                        .orElseThrow(() ->
                                new InvalidInvitationException(
                                        "Invitation token is invalid"
                                ));

        if (invitation.getUsed() == true) {
            throw new InvalidInvitationException("Invitation has already been used");
        }

        if (invitation.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new InvalidInvitationException("Invitation has expired");
        }

        User user =
                userRepository.findByEmail(invitation.getUser().getEmail())
                        .orElseThrow(() ->
                                new UserNotFoundException(
                                        "User not found"
                                ));

        keycloakAdminService.setPassword(user.getKeycloakUserId(), request.password());
        keycloakAdminService.markEmailAsVerified(user.getKeycloakUserId());

        user.setIsActive(true);
        invitation.setUsed(true);

        userRepository.save(user);
        userInvitationRepository.save(invitation);
    }

    public InvitationValidationResponse validateInvitation(String token) {
        UserInvitation invitation =
                userInvitationRepository.findByToken(token)
                        .orElseThrow(() ->
                                new InvalidInvitationException(
                                        "Invitation token is invalid"
                                ));

        if (invitation.getUsed() == true) {
            throw new InvalidInvitationException(
                    "Invitation has already been used"
            );
        }

        if (invitation.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new InvalidInvitationException(
                    "Invitation has expired"
            );
        }

        User user = invitation.getUser();

        return new InvitationValidationResponse(
                true,
                user.getLastName(),
                user.getLastName(),
                user.getEmail()
        );

    }

    @Transactional
    public void resendInvitation(UUID userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "User not found"
                        ));

        if (user.getIsActive() == true) {
            throw new InvalidInvitationException("User has already accepted the invitation");
        }

        userInvitationRepository.invalidateUserInvitations(userId);

        invitationService.createAndSendInvitation(user);
    }

    @Transactional
    public UserResponse updateUser(UUID userId, UpdateUserRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (request.email() != null) {
            user.setEmail(request.email());
        }

        if (request.firstName() != null) {
            user.setFirstName(request.firstName());
        }

        if (request.lastName() != null) {
            user.setLastName(request.lastName());
        }

        if (request.isActive() != null) {
            user.setIsActive(request.isActive());
        }

        User saved = userRepository.save(user);

        return UserResponse.fromEntity(saved);
    }

    public Page<UserResponse> searchUsers(UserSearchRequest request) {
        Specification<User> specification = UserSpecification.search(request);

        String sortBy = ALLOWED_SORT_FIELDS.contains(request.sortBy()) ? request.sortBy() : "createdAt";

        Sort sort = Sort.by(
                request.sortDirection(),
                sortBy
        );

        Pageable pageable = PageRequest.of(request.page(), request.size(), sort);

        return userRepository
                .findAll(specification, pageable)
                .map(UserResponse::fromEntity);
    }

    public UserIdsResponse searchUserIds(
            UserSearchIdsRequest request
    ) {

        return new UserIdsResponse(
                userRepository.searchUserIds(
                        request.keyword()
                )
        );
    }

    public UserBatchResponse getUsersByIds(List<UUID> userIds) {

        List<UserSummaryResponse> users = userRepository.findAllById(userIds)
                .stream()
                .map(this::mapToUserSummary)
                .toList();

        return new UserBatchResponse(users);
    }

    public UserResponse getUserDetails(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        return UserResponse.fromEntity(user);
    }

    public UserSummaryResponse getUserSummary(UUID userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        return new UserSummaryResponse(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getIsActive()
        );
    }

    public UserSummaryResponse getUserSummaryByKeycloakId(String keycloakUserId) {

        User user = userRepository.findByKeycloakUserId(keycloakUserId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        return new UserSummaryResponse(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getIsActive()
        );
    }

    @Transactional
    public UserResponse updateUserStatus(
            UUID userId,
            boolean active
    ) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found"));

        user.setIsActive(active);

        keycloakAdminService.updateUserStatus(user.getKeycloakUserId(), active);

        return UserResponse.fromEntity(
                userRepository.save(user)
        );
    }

    public AuthenticatedUserResponse getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof Jwt jwt)) {
            throw new IllegalStateException("Invalid authentication");
        }

        String keycloakUserId = jwt.getSubject();

        String email = jwt.getClaimAsString("email");
        String firstName = jwt.getClaimAsString("given_name");
        String lastName = jwt.getClaimAsString("family_name");

        Map<String, Object> realmAccess = jwt.getClaim("realm_access");

        @SuppressWarnings("unchecked")
        List<String> roles = (List<String>) realmAccess.get("roles");

        Set<String> permissions = roles.stream()
                .flatMap(role -> UserRolePermissionMapping.ROLE_PERMISSIONS
                        .getOrDefault(role, List.of())
                        .stream())
                .map(Enum::name)
                .collect(Collectors.toSet());

        User user = this.getOrCreateUser(keycloakUserId, email, firstName, lastName);

        return new AuthenticatedUserResponse(
                user.getId(),
                keycloakUserId,
                email,
                user.getFirstName(),
                user.getLastName(),
                permissions
        );

    }

    public User getOrCreateUser(String keycloakUserId, String email, String firstName, String lastName) {
        return userRepository.findByKeycloakUserId(keycloakUserId)
                .orElseGet(() -> {

                    User user = new User();
                    user.setKeycloakUserId(keycloakUserId);
                    user.setEmail(email);

                    user.setFirstName(firstName);
                    user.setLastName(lastName);

                    user.setIsActive(true);

                    return userRepository.save(user);
                });
    }

    private UserSummaryResponse mapToUserSummary(User user) {

        return new UserSummaryResponse(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getIsActive()
        );
    }

    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
            "firstName",
            "lastName",
            "email",
            "createdAt"
    );
}
