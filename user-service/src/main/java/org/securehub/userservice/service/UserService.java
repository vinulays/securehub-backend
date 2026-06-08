package org.securehub.userservice.service;

import lombok.RequiredArgsConstructor;
import org.securehub.userservice.dto.CreateUserRequest;
import org.securehub.userservice.dto.UpdateUserRequest;
import org.securehub.userservice.dto.UserResponse;
import org.securehub.userservice.dto.UserSearchRequest;
import org.securehub.userservice.entity.User;
import org.securehub.userservice.enums.RolePermissionMapping;
import org.securehub.userservice.exception.UserAlreadyExistsException;
import org.securehub.userservice.exception.UserNotFoundException;
import org.securehub.userservice.model.AuthenticatedUser;
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

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserResponse createUser(CreateUserRequest request){

        boolean exists = userRepository.existsByEmail(request.email());

        if(exists){
            throw new UserAlreadyExistsException("User already exists with email" );
        }

        User user = new User();

        user.setKeycloakUserId(request.keycloakUserId());
        user.setEmail(request.email());
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setIsActive(request.isActive());

        User saved = userRepository.save(user);

        return UserResponse.fromEntity(saved);
    }

    public UserResponse updateUser(UUID userId, UpdateUserRequest request){
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
