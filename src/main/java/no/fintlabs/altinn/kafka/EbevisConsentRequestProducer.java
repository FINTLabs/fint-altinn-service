package no.fintlabs.altinn.kafka;

import lombok.extern.slf4j.Slf4j;
import no.fint.altinn.model.kafka.KafkaEvidenceConsentRequest;
import no.novari.kafka.producing.ParameterizedProducerRecord;
import no.novari.kafka.producing.ParameterizedTemplate;
import no.novari.kafka.producing.ParameterizedTemplateFactory;
import no.novari.kafka.topic.name.EventTopicNameParameters;
import no.novari.kafka.topic.name.TopicNamePrefixParameters;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EbevisConsentRequestProducer {

    @Value("${fint.org-id}")
    private String orgId;

    private final ParameterizedTemplate<KafkaEvidenceConsentRequest> parameterizedTemplate;

    public EbevisConsentRequestProducer(ParameterizedTemplateFactory eventProducerFactory) {
        parameterizedTemplate = eventProducerFactory.createTemplate(KafkaEvidenceConsentRequest.class);
    }

    public void publish(KafkaEvidenceConsentRequest consentRequest) {

        TopicNamePrefixParameters prefixParameters = TopicNamePrefixParameters.stepBuilder()
                .orgId(orgId.replace(".", "-"))
                .domainContextApplicationDefault()
                .build();

        EventTopicNameParameters topicNameParameters1 = EventTopicNameParameters.builder()
                .eventName("consent-request")
                .topicNamePrefixParameters(prefixParameters)
                .build();

        log.info("{}: Publishing consent request", consentRequest.getAltinnInstanceId());
        parameterizedTemplate.send(
                ParameterizedProducerRecord.<KafkaEvidenceConsentRequest>builder()
                        .topicNameParameters(topicNameParameters1)
                        .key(consentRequest.getAltinnInstanceId())
                        .value(consentRequest)
                        .build()
        );
    }
}

