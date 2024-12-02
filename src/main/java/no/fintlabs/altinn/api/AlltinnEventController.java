package no.fintlabs.altinn.api;

import lombok.extern.slf4j.Slf4j;
import no.fint.altinn.model.kafka.KafkaAltinnInstance;
import no.fintlabs.altinn.altinn.AltinnInstanceMapper;
import no.fintlabs.altinn.altinn.model.AltinnInstanceModel;
import no.fintlabs.altinn.altinn.model.ApplicationModel;
import no.fintlabs.altinn.kafka.InstancePublisherService;
import no.fintlabs.altinn.altinn.AltinnInstanceService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/api/event")
public class AlltinnEventController {

    private final AltinnInstanceService altinnInstanceService;
    private final InstancePublisherService instancePublisherService;

    public AlltinnEventController(AltinnInstanceService altinnInstanceService, InstancePublisherService instancePublisherService) {
        this.altinnInstanceService = altinnInstanceService;
        this.instancePublisherService = instancePublisherService;
    }

    @PostMapping("/instances")
    public void getAltinnInstances() {
        Mono<AltinnInstanceModel> instances = altinnInstanceService.getInstances();
        Mono<ApplicationModel> applicationData = altinnInstanceService.getApplicationData(instances);

        Mono.zip(instances, applicationData).doOnSuccess(tuple -> {
            KafkaAltinnInstance altinnInstance = AltinnInstanceMapper.mapToAltinnInstance(
                    tuple.getT1().getInstances().getFirst(),
                    tuple.getT2());
            instancePublisherService.publish(altinnInstance);
        })
                .doOnError(throwable -> log.error("Error: ", throwable))
                .subscribe(tuple -> log.info("Instance: {}, Datamodell XML: {}", tuple.getT1().getInstances().getFirst().getId(), tuple.getT2()));
    }

    @PostMapping("/instance/{partyId}/{instanceId}")
    public Mono<String> getAltinnInstance(@PathVariable String partyId, @PathVariable String instanceId) {
        return altinnInstanceService.getInstance(partyId.concat("/").concat(instanceId));
    }
}
