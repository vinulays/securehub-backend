package org.securehub.userservice.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.securehub.userservice.dto.*;
import org.securehub.userservice.entity.User;
import org.securehub.userservice.entity.UserInvitation;
import org.securehub.userservice.enums.RolePermissionMapping;
import org.securehub.userservice.exception.InvalidInvitationException;
import org.securehub.userservice.exception.UserAlreadyExistsException;
import org.securehub.userservice.exception.UserNotFoundException;
import org.securehub.userservice.model.AuthenticatedUser;
import org.securehub.userservice.producer.UserEventProducer;
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

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserInvitationRepository userInvitationRepository;
    private final KeycloakAdminService keycloakAdminService;
    private final UserEventProducer userEventProducer;
    private final TokenService tokenService;

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

        this.createAndSendInvitation(savedUser);

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

        this.createAndSendInvitation(user);
    }

    private void createAndSendInvitation(User user) {

        String token = tokenService.generateInvitationToken();

        UserInvitation invitation =
                UserInvitation.builder()
                        .token(token)
                        .user(user)
                        .expiresAt(LocalDateTime.now().plusDays(7))
                        .used(false)
                        .build();

        userInvitationRepository.save(invitation);

        userEventProducer.publishUserCreated(
                new UserCreatedEvent(
                        user.getId(),
                        user.getEmail(),
                        user.getFirstName(),
                        user.getLastName(),
                        token
                )
        );
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
                Sort.Direction.fromString(request.sortDirection().toString()),
                sortBy
        );

        Pageable pageable = PageRequest.of(request.page(), request.size(), sort);

        return userRepository
                .findAll(specification, pageable)
                .map(UserResponse::fromEntity);
    }

    public UserResponse getUserDetails(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        return UserResponse.fromEntity(user);
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

    public AuthenticatedUser getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Jwt jwt = (Jwt) authentication.getPrincipal();

        String keycloakUserId = jwt.getSubject();

        String email = jwt.getClaimAsString("email");
        String firstName = jwt.getClaimAsString("given_name");
        String lastName = jwt.getClaimAsString("family_name");

        Map<String, Object> realmAccess = jwt.getClaim("realm_access");

        List<String> roles =
                (List<String>) realmAccess.get("roles");

        Set<String> permissions = roles.stream()
                .flatMap(role -> RolePermissionMapping.ROLE_PERMISSIONS
                        .getOrDefault(role, List.of())
                        .stream())
                .map(Enum::name)
                .collect(Collectors.toSet());

        User user = this.getOrCreateUser(keycloakUserId, email, firstName, lastName);

        return new AuthenticatedUser(
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

    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
            "firstName",
            "lastName",
            "email",
            "createdAt"
    );
}
