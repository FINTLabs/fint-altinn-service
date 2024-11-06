package no.fintlabs.altinn.api;

import no.fintlabs.altinn.service.AltinnInstanceService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/event")
public class AlltinnEventController {

    private final AltinnInstanceService altinnInstanceService;

    public AlltinnEventController(AltinnInstanceService altinnInstanceService) {
        this.altinnInstanceService = altinnInstanceService;
    }

    @PostMapping("/instances")
    public Mono<String> getAltinnInstances() {
        return altinnInstanceService.getInstances();
    }

    @PostMapping("/instance/{partyId}/{instanceId}")
    public Mono<String> getAltinnInstance(@PathVariable String partyId, @PathVariable String instanceId) {
        return altinnInstanceService.getInstance(partyId.concat("/").concat(instanceId));
    }
}
