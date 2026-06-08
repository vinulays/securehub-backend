package org.securehub.userservice.service;

import lombok.RequiredArgsConstructor;
import org.securehub.userservice.entity.User;
import org.securehub.userservice.model.AuthenticatedUser;
import org.securehub.userservice.repository.UserRepository;
import org.securehub.userservice.role.RolePermissionMapping;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

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
}
