package no.novari.altinn.altinn;

import lombok.extern.slf4j.Slf4j;
import no.novari.altinn.maskinporten.MaskinportenService;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${fint.altinn.platform.base-url}")
    private String baseUrl;


    public AltinnWebClientConfig(MaskinportenService maskinportenService) {
        this.maskinportenService = maskinportenService;
    }

    @Bean
    public WebClient altinnWebClient() {
        return WebClient.builder()
                .filter(this::maskinportenAuthorization)
                .baseUrl(baseUrl)
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
        return WebClient.create(baseUrl).get()
                .uri("/authentication/api/v1/exchange/maskinporten")
                .header("Authorization", bearToken)
                .retrieve()
                .bodyToMono(String.class)
                .doOnError(error -> log.error("Error exchanging Maskinporten token for Altinn token", error));
    }
}
