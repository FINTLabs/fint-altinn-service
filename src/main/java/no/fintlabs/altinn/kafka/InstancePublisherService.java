package no.fintlabs.altinn.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class InstancePublisherService {

    private final KafkaTemplate<String, AltinnInstance> kafkaTemplate;
    private final KafkaTopicNameProperties topics;

    public InstancePublisherService(KafkaTemplate<String, AltinnInstance> kafkaTemplate, KafkaTopicNameProperties topics) {
        this.kafkaTemplate = kafkaTemplate;
        this.topics = topics;
    }

    public void publish(AltinnInstance altinnInstance) {
        String topicName = topics.getAltinnInstanceCreated();
        log.info("Publishing altinn instance to topic {}: {}", topicName, altinnInstance);
        kafkaTemplate
                .send(topicName, "123", altinnInstance)
                .thenAccept(result ->
                        log.info("💃 Published altinn instance to topic {}: {}", topicName, result))
                .exceptionally(e -> {
                    log.error("🤦 Failed to publish to topic={}", topicName, e);
                    if (e.getCause() != null) {
                        log.error("Cause: {}", e.getCause().getMessage());
                    }
                    return null;
                });
    }
}
