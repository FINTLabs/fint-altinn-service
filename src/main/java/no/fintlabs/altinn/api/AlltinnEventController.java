package no.fintlabs.altinn.api;

import no.fintlabs.altinn.kafka.AltinnInstance;
import no.fintlabs.altinn.kafka.InstancePublisherService;
import no.fintlabs.altinn.service.AltinnInstanceService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

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
    public Mono<String> getAltinnInstances() {
        Mono<String> instances = altinnInstanceService.getInstances();
        instances.doOnSuccess(inst ->
                        instancePublisherService
                                .publish(new AltinnInstance("111", "x", inst)))
                .block();
        return instances;
    }

    @PostMapping("/instance/{partyId}/{instanceId}")
    public Mono<String> getAltinnInstance(@PathVariable String partyId, @PathVariable String instanceId) {
        return altinnInstanceService.getInstance(partyId.concat("/").concat(instanceId));
    }
}
