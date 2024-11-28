package no.fintlabs.altinn.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class KafkaTopicConfig {

    private final KafkaTopicNameProperties topics;

    public KafkaTopicConfig(KafkaTopicNameProperties topics) {
        this.topics = topics;
    }

    @Bean
    NewTopic createAltinnInstancesTopic() {
        String topicName = topics.getAltinnInstanceCreated();
        log.info("Creating Kafka topic: {}", topicName);
        return new NewTopic(topicName, 1, (short) 1);
    }
}