package org.securehub.notificationservice.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.securehub.notificationservice.dto.UserCreatedEvent;
import org.securehub.notificationservice.service.EmailService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserCreatedConsumer {

    private final EmailService emailService;

    @KafkaListener(topics = "user.created", groupId = "notification-group")
    public void consumer(UserCreatedEvent event) {

        emailService.sendInvitationEmail(event);

        log.info("Invitation email sent to {}", event.email());
    }
}
