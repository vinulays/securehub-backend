package org.securehub.notificationservice.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.securehub.notificationservice.dto.MemberAddedEvent;
import org.securehub.notificationservice.service.EmailService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrganizationEventConsumer {

    private final EmailService emailService;

    @KafkaListener(
            topics = "organization.member.added",
            groupId = "notification-group",
            containerFactory = "memberAddedEventListenerFactory"
    )
    public void consumer(MemberAddedEvent event) {
        emailService.sendMemberAddedEmail(event);

        log.info("Member added email sent to {}", event.email());
    }


}
