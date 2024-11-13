package no.fintlabs.altinn.kafka;

import lombok.extern.slf4j.Slf4j;
import no.fintlabs.kafka.event.EventProducer;
import no.fintlabs.kafka.event.EventProducerFactory;
import no.fintlabs.kafka.event.EventProducerRecord;
import no.fintlabs.kafka.event.topic.EventTopicNameParameters;
import no.fintlabs.kafka.event.topic.EventTopicService;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Slf4j
@Service
public class InstancePublisherService {

    private final EventProducer<Object> instanceExampleEventProducer;
    private final EventTopicNameParameters eventTopicNameParameters;

    public InstancePublisherService(EventProducerFactory eventProducerFactory, EventTopicService eventTopicService) {
        this.instanceExampleEventProducer = eventProducerFactory.createProducer(Object.class);
        this.eventTopicNameParameters = EventTopicNameParameters.builder()
                .orgId("fintlabs-no")
                .eventName("altinn-instance")
                .domainContext("drosje")
                .build();
        log.info("Ensuring event topic: {}", eventTopicNameParameters);
        eventTopicService.ensureTopic(eventTopicNameParameters, Duration.ofDays(1).toMillis());
    }

    public void publish(AltinnInstance altinnInstance) {
        log.info("Publishing altinn instance: {}", altinnInstance);
        instanceExampleEventProducer.send(
                EventProducerRecord.builder()
                        .topicNameParameters(eventTopicNameParameters)
                        .value(altinnInstance)
                        .build());
    }

}
