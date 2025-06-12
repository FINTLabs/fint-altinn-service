package no.fintlabs.altinn.altinn;

import lombok.extern.slf4j.Slf4j;
import no.fintlabs.altinn.maskinporten.MaskinportenService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Configuration
public class AltinnWebClientConfig {

    private final MaskinportenService maskinportenService;

    public AltinnWebClientConfig(MaskinportenService maskinportenService) {
        this.maskinportenService = maskinportenService;
    }

    @Bean
    public WebClient altinnWebClient(WebClient.Builder builder) {
        return builder
                .filter(this::maskinportenAuthorization)
                .baseUrl("https://platform.tt02.altinn.no")
                .build();
    }

    private Mono<ClientResponse> maskinportenAuthorization(ClientRequest clientRequest, ExchangeFunction next) {
        return maskinportenService.getBearerToken()
                .flatMap(this::exchangeForAltinnToken)
                .flatMap(altinnToken -> next.exchange(
                        ClientRequest.from(clientRequest)
                            .header("Authorization", "Bearer " + altinnToken).build()));
    }

    private Mono<String> exchangeForAltinnToken(String bearToken) {
        return WebClient.create("https://platform.tt02.altinn.no").get()
                .uri("/authentication/api/v1/exchange/maskinporten")
                .header("Authorization", bearToken)
                .retrieve()
                .bodyToMono(String.class)
                .doOnError(error -> log.error("Error exchanging Maskinporten token for Altinn token", error));
    }
}
