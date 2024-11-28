package no.fintlabs.altinn.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class InstancePublisherService {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public InstancePublisherService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(AltinnInstance altinnInstance) {
        log.info("Publishing altinn instance: {}", altinnInstance);
        kafkaTemplate
                .send("altinn-instances", "123", "test")
                .thenAccept(result ->
                        log.info("💃 Published altinn instance: {}", result))
                .exceptionally(e -> {
                    log.error("🤦 Failed to publish to topic=altinn-instances", e);
                    if (e.getCause() != null) {
                        log.error("Cause: {}", e.getCause().getMessage());
                    }
                    return null;
                });
    }

}
