package org.securehub.userservice.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.securehub.userservice.entity.User;
import org.securehub.userservice.event.UserCreatedDomainEvent;
import org.securehub.userservice.exception.UserNotFoundException;
import org.securehub.userservice.service.InvitationService;
import org.securehub.userservice.repository.UserRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserCreatedEventListener {

    private final UserRepository userRepository;
    private final InvitationService invitationService;

    @TransactionalEventListener(
            phase = TransactionPhase.AFTER_COMMIT
    )
    public void handle(UserCreatedDomainEvent event) {

        User user = userRepository.findById(event.userId())
                .orElseThrow(() ->
                        new UserNotFoundException("User not found"));

        invitationService.createAndSendInvitation(user);
    }
}