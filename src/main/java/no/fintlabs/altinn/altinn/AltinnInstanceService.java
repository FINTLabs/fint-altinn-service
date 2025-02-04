package no.fintlabs.altinn.altinn;

import lombok.extern.slf4j.Slf4j;
import no.fintlabs.altinn.altinn.model.AltinnInstance;
import no.fintlabs.altinn.altinn.model.AltinnInstanceModel;
import no.fintlabs.altinn.altinn.model.ApplicationModel;
import org.springframework.http.MediaType;
import org.springframework.http.codec.xml.Jaxb2XmlDecoder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class AltinnInstanceService {

    private final WebClient webClient;

    public AltinnInstanceService(WebClient altinnwWebClient) {
        this.webClient = altinnwWebClient;
    }

    public Mono<AltinnInstanceModel> getInstances() {
        return webClient.get()
                .uri("/storage/api/v1/instances?appId=vigo/drosjesentral&status.isArchived=true")
                .retrieve().bodyToMono(AltinnInstanceModel.class);
    }

    public Mono<ApplicationModel> getApplicationData(AltinnInstance altinnInstance) {
        String uri = altinnInstance.getData().stream()
                    .filter(data -> data.getDataType().equals("Datamodell"))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("No matching Datamodell in instance"))
                    .getSelfLinks().get("platform");

            return webClient.mutate()
                    .exchangeStrategies(ExchangeStrategies.builder()
                            .codecs(configurer ->
                                configurer.defaultCodecs().jaxb2Decoder(new Jaxb2XmlDecoder())).build())
                    .build()
                    .get()
                    .uri(uri)
                    .accept(MediaType.APPLICATION_XML)
                    .retrieve()
                    .bodyToMono(ApplicationModel.class);
    }

    public Mono<String> getInstance(String instanceId) {
        return webClient.get()
                .uri("/storage/api/v1/instances/" + instanceId + "?appId=vigo/drosjesentral")
                .retrieve().bodyToMono(String.class);
    }

}
