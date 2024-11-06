package no.fintlabs.altinn.service;

import lombok.extern.slf4j.Slf4j;
import no.fintlabs.altinn.maskinporten.MaskinportenConfiguration;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class AltinnInstanceService {

    private final WebClient webClient;
    private final MaskinportenConfiguration maskinportenConfiguration;

    public AltinnInstanceService(WebClient webClient, MaskinportenConfiguration maskinportenConfiguration) {
        this.webClient = webClient;
        this.maskinportenConfiguration = maskinportenConfiguration;
    }

    public Mono<String> getInstances() {
        return maskinportenConfiguration.getBearerToken().flatMap(
                bearToken -> webClient.get()
                        .uri("https://platform.tt02.altinn.no/authentication/api/v1/exchange/maskinporten")
                        .header("Authorization", bearToken)
                        .retrieve()
                        .bodyToMono(String.class)
                        .flatMap(
                                token -> webClient.get()
                                        .uri("https://platform.tt02.altinn.no/storage/api/v1/instances?appId=vigo/drosjesentral")
                                        .header("Authorization", "Bearer " + token)
                                        .retrieve().bodyToMono(String.class)
                        )
        );
    }

    public Mono<String> getInstance(String instanceId) {
        return maskinportenConfiguration.getBearerToken().flatMap(
                bearToken -> webClient.get()
                        .uri("https://platform.tt02.altinn.no/authentication/api/v1/exchange/maskinporten")
                        .header("Authorization", bearToken)
                        .retrieve()
                        .bodyToMono(String.class)
                        .flatMap(
                                token -> webClient.get()
                                        .uri("https://platform.tt02.altinn.no/storage/api/v1/instances/" + instanceId + "?appId=vigo/drosjesentral")
                                        .header("Authorization", "Bearer " + token)
                                        .retrieve().bodyToMono(String.class)
                        )
        );
    }

}
