package no.fintlabs.altinn.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Slf4j
@Service
public class InstancePublisherService {

    private final KafkaTemplate<String, AltinnInstance> kafkaTemplate;

    public InstancePublisherService(KafkaTemplate<String, AltinnInstance> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(AltinnInstance altinnInstance) {
        log.info("Publishing altinn instance: {}", altinnInstance);
        kafkaTemplate.send("altinn-instances", "123", altinnInstance);
    }

}
