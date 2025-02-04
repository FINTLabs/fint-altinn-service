package no.fintlabs.altinn.api;

import lombok.extern.slf4j.Slf4j;
import no.fint.altinn.model.kafka.KafkaAltinnInstance;
import no.fintlabs.altinn.altinn.AltinnInstanceMapper;
import no.fintlabs.altinn.altinn.AltinnInstanceService;
import no.fintlabs.altinn.altinn.model.AltinnInstance;
import no.fintlabs.altinn.altinn.model.ApplicationModel;
import no.fintlabs.altinn.database.Instance;
import no.fintlabs.altinn.database.InstanceRepository;
import no.fintlabs.altinn.kafka.InstancePublisherService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.util.function.Consumer;

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
                .flatMapMany(altinnInstanceModel -> Flux.fromIterable(altinnInstanceModel.getInstances()))
                .filter(this::isNew)
                .map(altinnInstance -> {
                    log.info("New instance: {}", altinnInstance.getId());

                    Mono<ApplicationModel> applicationData = altinnInstanceService.getApplicationData(altinnInstance);

                    Mono.zip(Mono.just(altinnInstance), applicationData)
                            .doOnSuccess(publishAndSave())
                            .doOnError(throwable -> log.error("Error: ", throwable))
                            .subscribe(tuple ->
                                    log.info("Instance: {}, Datamodell XML: {}",
                                            tuple.getT1().getId(), tuple.getT2()));
                    return applicationData;
                })
                .subscribe();

    }

    private boolean isNew(AltinnInstance altinnInstanses) {
        return altinnRepository.findAllInstances().stream()
                .noneMatch(instance -> instance.getInstanceId().equals(altinnInstanses.getId()));
    }

    private Consumer<Tuple2<AltinnInstance, ApplicationModel>> publishAndSave() {
        return tuple -> {
            KafkaAltinnInstance altinnInstance = AltinnInstanceMapper.mapToAltinnInstance(
                    tuple.getT1(),
                    tuple.getT2());
            instancePublisherService.publish(altinnInstance);

            altinnRepository.saveInstance(
                    Instance.builder()
                            .instanceId(tuple.getT1().getId())
                            .completed(true)
                            .build());
        };
    }

    @PostMapping("/instance/{partyId}/{instanceId}")
    public Mono<String> getAltinnInstance(@PathVariable String partyId, @PathVariable String instanceId) {
        return altinnInstanceService.getInstance(partyId.concat("/").concat(instanceId));
    }
}
