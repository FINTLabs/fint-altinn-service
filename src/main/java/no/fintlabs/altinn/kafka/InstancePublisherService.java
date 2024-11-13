package no.fintlabs.altinn.kafka;

import lombok.extern.slf4j.Slf4j;
import no.fintlabs.kafka.event.EventProducer;
import no.fintlabs.kafka.event.EventProducerFactory;
import no.fintlabs.kafka.event.EventProducerRecord;
import no.fintlabs.kafka.event.topic.EventTopicNameParameters;
import no.fintlabs.kafka.event.topic.EventTopicService;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class InstancePublisherService {

    private final EventProducer<Object> instanceExampleEventProducer;
    private final EventTopicNameParameters eventTopicNameParameters;


    public InstancePublisherService(EventProducerFactory eventProducerFactory, EventTopicService eventTopicService) {
        this.instanceExampleEventProducer = eventProducerFactory.createProducer(Object.class);
        this.eventTopicNameParameters = EventTopicNameParameters.builder()
                .orgId("agderfk.no")
                .eventName("altinn-instance")
                .build();
        eventTopicService.ensureTopic(eventTopicNameParameters, 0);
    }

    public void publish(AltinnInstance altinnInstance) {
        instanceExampleEventProducer.send(
                EventProducerRecord.builder()
                        .topicNameParameters(eventTopicNameParameters)
                        .value(altinnInstance)
                        .build());
    }

}
