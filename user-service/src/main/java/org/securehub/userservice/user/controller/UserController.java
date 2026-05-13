package org.securehub.userservice.user.controller;

import lombok.RequiredArgsConstructor;
import org.securehub.userservice.security.model.AuthenticatedUser;
import org.securehub.userservice.security.service.CurrentUserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final CurrentUserService currentUserService;

    @GetMapping("/api/users/me")
    public AuthenticatedUser getCurrentUser(){

        return currentUserService.getCurrentUser();
    }
}
