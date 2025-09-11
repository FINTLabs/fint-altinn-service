package no.fintlabs.altinn.api;

import lombok.extern.slf4j.Slf4j;
import no.fint.altinn.model.kafka.KafkaAltinnInstance;
import no.fint.altinn.model.kafka.KafkaEvidenceConsentRequest;
import no.fintlabs.altinn.altinn.AltinnInstanceService;
import no.fintlabs.altinn.altinn.model.AltinnInstance;
import no.fintlabs.altinn.altinn.model.ApplicationModel;
import no.fintlabs.altinn.database.InstanceRepository;
import no.fintlabs.altinn.kafka.EbevisConsentRequestProducer;
import no.fintlabs.altinn.kafka.InstanceProducer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.util.AbstractMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static no.fintlabs.altinn.altinn.AltinnInstanceMapper.mapToAltinnInstance;

@Slf4j
@RestController
@RequestMapping("/api/event")
public class AlltinnEventController {

    private final AltinnInstanceService altinnInstanceService;
    private final InstanceRepository altinnRepository;
    private final EbevisConsentRequestProducer consentRequestProducer;

    @Value("${fint.org-id}")
    private String orgId;

    @Value("${fint.county-organization-number}")
    private String countyOrganizationNumber;

    @Value("${fint.county-number}")
    private String countyNumber;

    public AlltinnEventController(AltinnInstanceService altinnInstanceService,
                                  InstanceProducer instanceProducer,
                                  InstanceRepository altinnRepository, EbevisConsentRequestProducer consentRequestProducer) {
        this.altinnInstanceService = altinnInstanceService;
        this.altinnRepository = altinnRepository;
        this.consentRequestProducer = consentRequestProducer;

    }

    @PostMapping("/instances")
    public void getAltinnInstances() {
        altinnInstanceService.getInstances()
                .flatMapMany(Flux::fromIterable)
                .filter(this::isNew)
                .map(this::requestApplicationData)
                .map(tuple2 ->
                        tuple2
                                .filter(this::onlyInstancesForConfiguredCounty)
                                .map(this::publishEbevisConcentRequest)
                )
                .subscribe();
    }

    @PostMapping("/instance/{partyId}/{instanceId}")
    public void getAltinnInstance(@PathVariable String partyId, @PathVariable String instanceId) {
        altinnInstanceService.getInstance(partyId.concat("/").concat(instanceId))
                .flatMap(this::requestApplicationData)
                .filter(this::onlyInstancesForConfiguredCounty)
                .map(this::publishEbevisConcentRequest)
                .subscribe();
    }

    private boolean onlyInstancesForConfiguredCounty(Tuple2<AltinnInstance, ApplicationModel> tuple2) {
        return tuple2.getT2().getVirksomhet().getFylke().getFylkesnummer().equals(countyNumber);
    }

    private Mono<Tuple2<AltinnInstance, ApplicationModel>> requestApplicationData(AltinnInstance altinnInstance) {
        log.info("New instance: {}", altinnInstance.getId());

        return Mono.zip(
                Mono.just(altinnInstance),
                altinnInstanceService.getApplicationData(altinnInstance));
    }

    private Tuple2<AltinnInstance, ApplicationModel> publishEbevisConcentRequest(Tuple2<AltinnInstance, ApplicationModel> tuple) {

        KafkaAltinnInstance kafkaAltinnInstance = mapToAltinnInstance(tuple.getT1(), tuple.getT2());

        log.info("{}: New instance received from organizationName {} in county {}",
                kafkaAltinnInstance.getInstanceId(),
                kafkaAltinnInstance.getOrganizationName(),
                kafkaAltinnInstance.getCountyName());

        KafkaEvidenceConsentRequest kafkaEvidenceRequest = KafkaEvidenceConsentRequest.builder()
                .altinnInstanceId(kafkaAltinnInstance.getInstanceId())
                .organizationNumber(kafkaAltinnInstance.getOrganizationNumber())
                .fintOrgId(orgId)
                .countyOrganizationNumber(countyOrganizationNumber)
                .build();

        consentRequestProducer.publish(kafkaEvidenceRequest);

        return tuple;
    }

    private boolean isNew(AltinnInstance altinnInstanse) {
        return altinnRepository.findAllInstances().stream()
                .noneMatch(instance -> instance.getInstanceId().equals(altinnInstanse.getId()));
    }

    private static final Map<String, String> countyOrganizationMapping = Stream.of(
                    new AbstractMap.SimpleImmutableEntry<>("ofk.no", "930580783"), //Østfold: 930580694 (virker ikke)
                    new AbstractMap.SimpleImmutableEntry<>("afk.no", "930580783"), //Akershus
                    new AbstractMap.SimpleImmutableEntry<>("bfk.no", "930580260"), //Buskerud
                    new AbstractMap.SimpleImmutableEntry<>("bym.oslo.kommune.no", "958935420"), //Oslo
                    new AbstractMap.SimpleImmutableEntry<>("innlandetfylke.no", "920717152"), //Innlandet
                    new AbstractMap.SimpleImmutableEntry<>("vestfoldfylke.no", "929882385"), //Vestfold
                    new AbstractMap.SimpleImmutableEntry<>("telefmarkfylke.no", "929882989"), //Telemark
                    new AbstractMap.SimpleImmutableEntry<>("agderfk.no", "921707134"), //Agder
                    new AbstractMap.SimpleImmutableEntry<>("rogfk.no", "971045698"), //Rogaland
                    new AbstractMap.SimpleImmutableEntry<>("vlfk.no", "821311632"), //Vestland
                    new AbstractMap.SimpleImmutableEntry<>("mrfylke.no", "944183779"), //Møre og Romsdal
                    new AbstractMap.SimpleImmutableEntry<>("trondelagfylke.no", "817920632"), //Trøndelang
                    new AbstractMap.SimpleImmutableEntry<>("nfk.no", "964982953"), //Nordland
                    new AbstractMap.SimpleImmutableEntry<>("tromsfylke.no", "930068128"), //Troms
                    new AbstractMap.SimpleImmutableEntry<>("ffk.no", "830090282")) //Finnmark
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
}
