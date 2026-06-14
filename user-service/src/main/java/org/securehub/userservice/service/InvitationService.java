package org.securehub.userservice.service;

import lombok.RequiredArgsConstructor;
import org.securehub.userservice.dto.event.UserCreatedEvent;
import org.securehub.userservice.entity.User;
import org.securehub.userservice.entity.UserInvitation;
import org.securehub.userservice.producer.UserEventProducer;
import org.securehub.userservice.repository.UserInvitationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class InvitationService {

    private final UserInvitationRepository userInvitationRepository;
    private final UserEventProducer userEventProducer;
    private final TokenService tokenService;

    @Transactional
    public void createAndSendInvitation(User user) {

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
}
