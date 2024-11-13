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
    private final MaskinportenService maskinportenService;
    private final InstancePublisherService instancePublisherService;

    public AltinnInstanceService(WebClient webClient, MaskinportenService maskinportenService, InstancePublisherService instancePublisherService) {
        this.webClient = webClient;
        this.maskinportenService = maskinportenService;
        this.instancePublisherService = instancePublisherService;
    }

    public Mono<String> getInstances() {

        instancePublisherService.publish(new AltinnInstance("123", "456"));

        return maskinportenService.getBearerToken()
                .flatMap(this::exchangeToken)
                .flatMap(this::fetchAltinnInstances);
    }

    public Mono<String> getInstance(String instanceId) {
        return maskinportenService.getBearerToken()
                .flatMap(this::exchangeToken)
                .flatMap(token -> fetchAltinnInstance(instanceId, token));
    }

    private Mono<String> fetchAltinnInstances(String token) {
        return webClient.get()
                .uri("https://platform.tt02.altinn.no/storage/api/v1/instances?appId=vigo/drosjesentral")
                .header("Authorization", "Bearer " + token)
                .retrieve().bodyToMono(String.class);
    }

    private Mono<String> fetchAltinnInstance(String instanceId, String token) {
        return webClient.get()
                .uri("https://platform.tt02.altinn.no/storage/api/v1/instances/" + instanceId + "?appId=vigo/drosjesentral")
                .header("Authorization", "Bearer " + token)
                .retrieve().bodyToMono(String.class);
    }

    private Mono<String> exchangeToken(String bearToken) {
        return webClient.get()
                .uri("https://platform.tt02.altinn.no/authentication/api/v1/exchange/maskinporten")
                .header("Authorization", bearToken)
                .retrieve()
                .bodyToMono(String.class);
    }

}
