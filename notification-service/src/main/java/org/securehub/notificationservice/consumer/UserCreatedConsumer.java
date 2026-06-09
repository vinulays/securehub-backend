package org.securehub.notificationservice.consumer;

import lombok.extern.slf4j.Slf4j;
import org.securehub.notificationservice.dto.UserCreatedEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserCreatedConsumer {

    @KafkaListener(topics = "user.created", groupId = "notification-group")
    public void consumer(UserCreatedEvent event){

        log.info("Received user created event {}", event);
    }
}
