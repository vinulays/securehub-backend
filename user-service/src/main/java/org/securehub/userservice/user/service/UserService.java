package org.securehub.userservice.user.service;

import lombok.RequiredArgsConstructor;
import org.securehub.userservice.user.entity.User;
import org.securehub.userservice.user.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

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
