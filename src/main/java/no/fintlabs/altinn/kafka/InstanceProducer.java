package no.fintlabs.altinn.kafka;

import lombok.extern.slf4j.Slf4j;
import no.fint.altinn.model.kafka.KafkaAltinnInstance;
import no.fintlabs.kafka.model.ParameterizedProducerRecord;
import no.fintlabs.kafka.producing.ParameterizedTemplate;
import no.fintlabs.kafka.producing.ParameterizedTemplateFactory;
import no.fintlabs.kafka.topic.name.EventTopicNameParameters;
import no.fintlabs.kafka.topic.name.TopicNamePrefixParameters;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class InstanceProducer {

    private final KafkaAdmin kafkaAdmin;
    private final KafkaTemplate<String, KafkaAltinnInstance> kafkaTemplate;
    private final KafkaTopicNameProperties topics;
    private ParameterizedTemplate<KafkaAltinnInstance> parameterizedTemplate;

    public InstanceProducer(KafkaAdmin kafkaAdmin,
                            KafkaTemplate<String,KafkaAltinnInstance> kafkaTemplate,
                            KafkaTopicNameProperties topics,
                            ParameterizedTemplateFactory parameterizedTemplateFactory) {
        this.kafkaAdmin = kafkaAdmin;
        this.kafkaTemplate = kafkaTemplate;
        this.topics = topics;
        parameterizedTemplate = parameterizedTemplateFactory.createTemplate(KafkaAltinnInstance.class);
    }

    public void publish(KafkaAltinnInstance altinnInstance) {

        TopicNamePrefixParameters prefixParameters = TopicNamePrefixParameters.builder()
                .orgIdApplicationDefault()
                .domainContextApplicationDefault()
                .build();

        EventTopicNameParameters topicParameters = EventTopicNameParameters.builder()
                .eventName("instance-received")
                .topicNamePrefixParameters(prefixParameters)
                .build();

        log.info("Publishing altinn instance to topic {}: {}", topicParameters.toString(), altinnInstance);

        ParameterizedProducerRecord<KafkaAltinnInstance> producerRecord = ParameterizedProducerRecord.<KafkaAltinnInstance>builder()
                .topicNameParameters(topicParameters)
                .key(altinnInstance.getInstanceId())
                .value(altinnInstance)
                .build();

        parameterizedTemplate.send(producerRecord)
                .thenAccept(result ->
                        log.info("💃 Published altinn instance to topic {}: {}", topicParameters.toString(), result))

                .exceptionally(e -> {
                    log.error("🤦 Failed to publish to topic={}", topicParameters.toString(), e);
                    if (e.getCause() != null) {
                        log.error("Cause: {}", e.getCause().getMessage());
                    }
                    return null;
                });
    }
}
