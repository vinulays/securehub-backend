package org.securehub.organizationservice.producer;

import lombok.RequiredArgsConstructor;
import org.securehub.organizationservice.dto.event.MemberAddedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrganizationEventProducer {

    private final KafkaTemplate<String, MemberAddedEvent> memberAddedEventKafkaTemplate;

    public void publishMemberAdded(MemberAddedEvent event) {

        memberAddedEventKafkaTemplate.send(
                "organization.member.added",
                event.userId().toString(),
                event
        );
    }
}
