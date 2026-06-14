package org.securehub.notificationservice.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.securehub.notificationservice.dto.MemberAddedEvent;
import org.securehub.notificationservice.dto.UserCreatedEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public ConsumerFactory<String, MemberAddedEvent> memberAddedEventConsumerFactory() {
        return consumerFactory(MemberAddedEvent.class);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, MemberAddedEvent> memberAddedEventListenerFactory() {
        return listenerFactory(memberAddedEventConsumerFactory());
    }

    @Bean
    public ConsumerFactory<String, UserCreatedEvent> userCreatedEventConsumerFactory() {
        return consumerFactory(UserCreatedEvent.class);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, UserCreatedEvent> userCreatedEventListenerFactory() {
        return listenerFactory(userCreatedEventConsumerFactory());
    }

    private Map<String, Object> consumerProps() {
        Map<String, Object> props = new HashMap<>();

        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "notification-group");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JacksonJsonDeserializer.class);

        return props;
    }

    private <T> ConsumerFactory<String, T> consumerFactory(Class<T> clazz) {

        JacksonJsonDeserializer<T> deserializer = new JacksonJsonDeserializer<>(clazz);

        deserializer.setRemoveTypeHeaders(false);
        deserializer.addTrustedPackages("*");
        deserializer.setUseTypeMapperForKey(true);

        return new DefaultKafkaConsumerFactory<>(
                consumerProps(),
                new StringDeserializer(),
                deserializer
        );
    }

    private <T> ConcurrentKafkaListenerContainerFactory<String, T> listenerFactory(
            ConsumerFactory<String, T> consumerFactory
    ) {
        ConcurrentKafkaListenerContainerFactory<String, T> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(consumerFactory);

        return factory;
    }
}
