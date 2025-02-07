package no.fintlabs.altinn.api;

import lombok.extern.slf4j.Slf4j;
import no.fint.altinn.model.kafka.KafkaAltinnInstance;
import no.fintlabs.altinn.altinn.AltinnInstanceMapper;
import no.fintlabs.altinn.altinn.AltinnInstanceService;
import no.fintlabs.altinn.altinn.model.AltinnInstance;
import no.fintlabs.altinn.altinn.model.ApplicationModel;
import no.fintlabs.altinn.database.Instance;
import no.fintlabs.altinn.database.InstanceFile;
import no.fintlabs.altinn.database.InstanceRepository;
import no.fintlabs.altinn.kafka.InstancePublisherService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/event")
public class AlltinnEventController {

    private final AltinnInstanceService altinnInstanceService;
    private final InstancePublisherService instancePublisherService;
    private final InstanceRepository altinnRepository;

    public AlltinnEventController(AltinnInstanceService altinnInstanceService, InstancePublisherService instancePublisherService, InstanceRepository altinnRepository) {
        this.altinnInstanceService = altinnInstanceService;
        this.instancePublisherService = instancePublisherService;
        this.altinnRepository = altinnRepository;
    }

    @PostMapping("/instances")
    public void getAltinnInstances() {
        altinnInstanceService.getInstances()
                .flatMapMany(Flux::fromIterable)
                .filter(this::isNew)
                .map(this::processInstance)
                .subscribe();
    }

    @PostMapping("/instance/{partyId}/{instanceId}")
    public void getAltinnInstance(@PathVariable String partyId, @PathVariable String instanceId) {
        altinnInstanceService.getInstance(partyId.concat("/").concat(instanceId))
                .map(this::processInstance)
                .subscribe();
    }

    private Mono<ApplicationModel> processInstance(AltinnInstance altinnInstance) {
        log.info("New instance: {}", altinnInstance.getId());

        Mono<ApplicationModel> applicationData = altinnInstanceService.getApplicationData(altinnInstance);

        Mono.zip(Mono.just(altinnInstance), applicationData)
                .doOnSuccess(this::publishAndSave)
                .doOnError(throwable -> log.error("Error: ", throwable))
                .subscribe(tuple ->
                        log.info("Instance: {}, Datamodell XML: {}",
                                tuple.getT1().getId(), tuple.getT2()));

        return applicationData;
    }

    private boolean isNew(AltinnInstance altinnInstanse) {
        return altinnRepository.findAllInstances().stream()
                .noneMatch(instance -> instance.getInstanceId().equals(altinnInstanse.getId()));
    }

    private void publishAndSave(Tuple2<AltinnInstance, ApplicationModel> tuple) {
        AltinnInstance altinnInstance = tuple.getT1();
        ApplicationModel applicationModel = tuple.getT2();

        KafkaAltinnInstance kafkaAltinnInstance = AltinnInstanceMapper.mapToAltinnInstance(altinnInstance, applicationModel);
        instancePublisherService.publish(kafkaAltinnInstance);

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

        altinnRepository.saveInstance(instance);
    }

}
