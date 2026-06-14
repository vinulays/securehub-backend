package org.securehub.userservice.producer;

import lombok.RequiredArgsConstructor;
import org.securehub.userservice.dto.event.UserCreatedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserEventProducer {

    private final KafkaTemplate<String, UserCreatedEvent> userInvitationCreatedEventKafkaTemplate;

    public void publishUserCreated(UserCreatedEvent event) {

        userInvitationCreatedEventKafkaTemplate.send(
                "user.created",
                event.userId().toString(),
                event
        );
    }
}

