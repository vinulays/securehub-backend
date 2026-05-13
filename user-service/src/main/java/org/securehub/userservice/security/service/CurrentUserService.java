package org.securehub.userservice.security.service;

import lombok.RequiredArgsConstructor;
import org.securehub.userservice.security.model.AuthenticatedUser;
import org.securehub.userservice.user.entity.User;
import org.securehub.userservice.user.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CurrentUserService {

    private final UserService userService;

    public AuthenticatedUser getCurrentUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Jwt jwt = (Jwt) authentication.getPrincipal();

        String keycloakUserId = jwt.getSubject();

        String email = jwt.getClaimAsString("email");
        String firstName = jwt.getClaimAsString("given_name");
        String lastName = jwt.getClaimAsString("family_name");

        User user = userService.getOrCreateUser(keycloakUserId, email, firstName, lastName);

        return new AuthenticatedUser(
                user.getId(),
                keycloakUserId,
                email,
                null
        );

    }
}
