package org.securehub.userservice.producer;

import lombok.RequiredArgsConstructor;
import org.securehub.userservice.dto.event.UserInvitationCreatedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishUserCreated(UserInvitationCreatedEvent event) {

        kafkaTemplate.send(
                "user.created",
                event.userId().toString(),
                event
        );
    }
}

