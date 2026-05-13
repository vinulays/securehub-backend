package org.securehub.userservice.security.service;

import org.securehub.userservice.security.model.AuthenticatedUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CurrentUserService {

    public AuthenticatedUser getCurrentUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Jwt jwt = (Jwt) authentication.getPrincipal();

        String keycloakUserId = jwt.getSubject();

        String email = jwt.getClaimAsString("email");

        List<String> roles =
                jwt.getClaimAsStringList("groups");

        return new AuthenticatedUser(
                null,
                keycloakUserId,
                email,
                roles
        );

    }
}
