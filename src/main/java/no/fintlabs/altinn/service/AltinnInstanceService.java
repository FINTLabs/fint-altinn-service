package no.fintlabs.altinn.service;

import lombok.extern.slf4j.Slf4j;
import no.fintlabs.altinn.kafka.AltinnInstance;
import no.fintlabs.altinn.kafka.InstancePublisherService;
import no.fintlabs.altinn.maskinporten.MaskinportenService;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class AltinnInstanceService {

    private final WebClient webClient;

    public AltinnInstanceService(WebClient altinnwWebClient) {
        this.webClient = altinnwWebClient;
    }

    public Mono<String> getInstances() {
        return webClient.get()
                .uri("/storage/api/v1/instances?appId=vigo/drosjesentral")
                .retrieve().bodyToMono(String.class);
    }

    public Mono<String> getInstance(String instanceId) {
        return webClient.get()
                .uri("/storage/api/v1/instances/" + instanceId + "?appId=vigo/drosjesentral")
                .retrieve().bodyToMono(String.class);
    }

}
