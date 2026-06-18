package org.securehub.userservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.securehub.userservice.entity.User;
import org.securehub.userservice.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class KeycloakBackfillService {

    private final UserRepository userRepository;
    private final KeycloakAdminService keycloakAdminService;

    public void backfillUserIds() {

        List<User> users = userRepository.findAll();

        for (User user : users) {

            keycloakAdminService.updateUserAttribute(
                    user.getKeycloakUserId(),
                    user.getId(),
                    "user_id"
            );
        }
    }
}
