package no.fintlabs.altinn.kafka;

import lombok.extern.slf4j.Slf4j;
import no.fint.altinn.model.kafka.KafkaAltinnInstance;
import no.fint.altinn.model.kafka.KafkaEvidenceConsentAccepted;
import no.fintlabs.altinn.altinn.AltinnInstanceService;
import no.fintlabs.altinn.altinn.model.AltinnInstance;
import no.fintlabs.altinn.altinn.model.ApplicationModel;
import no.fintlabs.altinn.database.Instance;
import no.fintlabs.altinn.database.InstanceFile;
import no.fintlabs.altinn.database.InstanceRepository;
import no.fintlabs.kafka.consuming.ListenerConfiguration;
import no.fintlabs.kafka.consuming.OffsetSeekingTrigger;
import no.fintlabs.kafka.consuming.ParameterizedListenerContainerFactoryService;
import no.fintlabs.kafka.topic.EventTopicService;
import no.fintlabs.kafka.topic.configuration.CleanupFrequency;
import no.fintlabs.kafka.topic.configuration.EventTopicConfiguration;
import no.fintlabs.kafka.topic.name.EventTopicNameParameters;
import no.fintlabs.kafka.topic.name.TopicNamePrefixParameters;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.listener.CommonLoggingErrorHandler;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.time.Duration;
import java.util.List;

import static no.fintlabs.altinn.altinn.AltinnInstanceMapper.mapToAltinnInstance;

@Slf4j
@Component
public class EbevisConsentAcceptedConsumer {

    private final EventTopicService eventTopicService;
    private final AltinnInstanceService altinnInstanceService;
    private final InstanceProducer instanceProducer;
    private final InstanceRepository instanceRepository;

    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

    public EbevisConsentAcceptedConsumer(EventTopicService eventTopicService, AltinnInstanceService altinnInstanceService, InstanceProducer instanceProducer, InstanceRepository instanceRepository) {
        this.eventTopicService = eventTopicService;
        this.altinnInstanceService = altinnInstanceService;
        this.instanceProducer = instanceProducer;
        this.instanceRepository = instanceRepository;
    }

    @Bean
    public ConcurrentMessageListenerContainer<String, KafkaEvidenceConsentAccepted> kafkaListenerContainerFactory(
            ParameterizedListenerContainerFactoryService parameterizedListenerContainerFactoryService) {

        TopicNamePrefixParameters topicNamePrefixParameters = TopicNamePrefixParameters.builder()
                .orgIdApplicationDefault()
                .domainContextApplicationDefault()
                .build();

        EventTopicNameParameters eventTopicNameParameters = EventTopicNameParameters
                .builder()
                .eventName("consent-accepted")
                .topicNamePrefixParameters(topicNamePrefixParameters)
                .build();

        EventTopicConfiguration eventTopicConfiguration = EventTopicConfiguration.builder()
                .retentionTime(Duration.ZERO)
                .cleanupFrequency(CleanupFrequency.NORMAL)
                .build();

        eventTopicService.createOrModifyTopic(eventTopicNameParameters, eventTopicConfiguration);

        ListenerConfiguration<KafkaEvidenceConsentAccepted> listenerConfiguration = ListenerConfiguration.builder(KafkaEvidenceConsentAccepted.class)
                .groupIdApplicationDefault()
                .maxPollRecordsKafkaDefault()
                .maxPollIntervalKafkaDefault()
                .errorHandler(new CommonLoggingErrorHandler())
                .continueFromPreviousOffsetOnAssignment()
                .offsetSeekingTrigger(new OffsetSeekingTrigger())
                .build();

        ConcurrentMessageListenerContainer<String, KafkaEvidenceConsentAccepted> container =
                parameterizedListenerContainerFactoryService.createRecordListenerContainerFactory(
                        this::processMessage,
                        listenerConfiguration)
                .createContainer(eventTopicNameParameters);

        container.setAutoStartup(true);

        return container;
    }

    private void processMessage(ConsumerRecord<String, KafkaEvidenceConsentAccepted> message) {
        KafkaEvidenceConsentAccepted consentAccepted = message.value();
        log.info("Received consent accepted: {}", consentAccepted);

        altinnInstanceService.getInstance(message.value().getAltinnInstanceId())
                .flatMap(this::addApplicationMetadata)
                .doOnSuccess(this::publishAndSave)
                .subscribe();

    }

    private void publishAndSave(Tuple2<AltinnInstance, ApplicationModel> altinnInstanceAndModel) {
        AltinnInstance altinnInstance = altinnInstanceAndModel.getT1();
        ApplicationModel applicationModel = altinnInstanceAndModel.getT2();

        log.info("Publishing instance: {}", altinnInstance.getId());

        KafkaAltinnInstance kafkaAltinnInstance = mapToAltinnInstance(altinnInstance, applicationModel);
        instanceProducer.publish(kafkaAltinnInstance);

        Instance instance = Instance.builder()
                .instanceId(altinnInstance.getId())
                .completed(true)
                .fintOrgId(kafkaAltinnInstance.getFintOrgId())
                .build();

        List<InstanceFile> files = altinnInstance.getData().stream()
                .filter(altinnData -> altinnData.getDataType().startsWith("FileUpload-") || altinnData.getDataType().contains("ref-data-as-pdf"))
                .map(altinnData ->
                        InstanceFile.builder()
                                .dataType(altinnData.getDataType().replace("FileUpload-", ""))
                                .url(altinnData.getSelfLinks().get("platform"))
                                .contentType(altinnData.getContentType())
                                .fileName(altinnData.getFilename())
                                .instance(instance)
                                .build())
                .toList();

        instance.setFiles(files);

        instanceRepository.saveInstance(instance);
    }

    private Mono<Tuple2<AltinnInstance, ApplicationModel>> addApplicationMetadata(AltinnInstance altinnInstance) {
        Mono<ApplicationModel> applicationData = altinnInstanceService.getApplicationData(altinnInstance);

        return Mono.zip(Mono.just(altinnInstance), applicationData);

    }

}
