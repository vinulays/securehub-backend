package org.securehub.userservice.controller;

import lombok.RequiredArgsConstructor;
import org.securehub.userservice.model.AuthenticatedUser;
import org.securehub.userservice.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public AuthenticatedUser getCurrentUser(){

        return userService.getCurrentUser();
    }
}
