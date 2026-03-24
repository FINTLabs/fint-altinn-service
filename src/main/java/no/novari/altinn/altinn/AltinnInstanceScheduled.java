package no.novari.altinn.altinn;

import lombok.extern.slf4j.Slf4j;
import no.fint.altinn.model.kafka.KafkaAltinnInstance;
import no.fint.altinn.model.kafka.KafkaEvidenceConsentRequest;
import no.novari.altinn.altinn.model.AltinnApplicationModel;
import no.novari.altinn.altinn.model.AltinnInstance;
import no.novari.altinn.database.Instance;
import no.novari.altinn.database.InstanceRepository;
import no.novari.altinn.kafka.EbevisConsentRequestProducer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import static no.novari.altinn.altinn.AltinnInstanceMapper.mapToAltinnInstance;

@Slf4j
@Component
public class AltinnInstanceScheduled {

    @Value("${fint.county-number}")
    private String countyNumber;

    @Value("${fint.org-id}")
    private String orgId;

    @Value("${fint.county-organization-number}")
    private String countyOrganizationNumber;

    private final AltinnInstanceService altinnInstanceService;
    private final InstanceRepository instanceRepository;
    private final EbevisConsentRequestProducer consentRequestProducer;

    public AltinnInstanceScheduled(AltinnInstanceService altinnInstanceService, InstanceRepository instanceRepository,
                                   EbevisConsentRequestProducer consentRequestProducer) {
        this.altinnInstanceService = altinnInstanceService;
        this.instanceRepository = instanceRepository;
        this.consentRequestProducer = consentRequestProducer;
    }

    @Scheduled(cron = "0 */10 * * * ?")
    public void getAltinnInstances() {
        log.info("⏱️ Scheduled fetching of Altinn instances");

        altinnInstanceService.getInstances()
                .flatMapMany(Flux::fromIterable)
                .filter(this::isNew)
                .flatMap(this::requestApplicationData)
                .filter(this::onlyInstancesForConfiguredCounty)
                .doOnNext(this::publishEbevisConcentRequest)
                .collectList()
                .subscribe(
                        list -> log.info("Processed {} new altinn instance(s).", list.size()),
                        error -> log.error("Error processing Altinn instances.", error)
                );
    }

    private boolean isNew(AltinnInstance altinnInstance) {
        return instanceRepository.findAllInstances().stream()
                .noneMatch(instance -> instance.getInstanceId().equals(altinnInstance.getId()));
    }

    private Mono<Tuple2<AltinnInstance, AltinnApplicationModel>> requestApplicationData(AltinnInstance altinnInstance) {
        return Mono.zip(Mono.just(altinnInstance), altinnInstanceService.getApplicationData(altinnInstance));
    }

    private boolean onlyInstancesForConfiguredCounty(Tuple2<AltinnInstance, ? extends AltinnApplicationModel> tuple2) {
        return tuple2.getT2().getVirksomhet().getFylke().getFylkesnummer().equals(countyNumber);
    }

    // TODO Consider refactor to void (some day) as we never use the return value
    private Tuple2<AltinnInstance, ? extends AltinnApplicationModel> publishEbevisConcentRequest(Tuple2<AltinnInstance, ? extends AltinnApplicationModel> tuple) {

        KafkaAltinnInstance kafkaAltinnInstance = mapToAltinnInstance(tuple.getT1(), tuple.getT2());

        log.info("{}: New instance with appId {} received from organizationNumber {} with organizationName {} in county {} and orgId {}",
                kafkaAltinnInstance.getInstanceId(),
                kafkaAltinnInstance.getAppId(),
                kafkaAltinnInstance.getOrganizationNumber(),
                kafkaAltinnInstance.getOrganizationName(),
                kafkaAltinnInstance.getCountyName(),
                orgId);

        KafkaEvidenceConsentRequest kafkaEvidenceRequest = KafkaEvidenceConsentRequest.builder()
                .altinnInstanceId(kafkaAltinnInstance.getInstanceId())
                .altinnAppId(kafkaAltinnInstance.getAppId())
                .organizationNumber(kafkaAltinnInstance.getOrganizationNumber())
                .organizationName(kafkaAltinnInstance.getOrganizationName())
                .fintOrgId(orgId)
                .countyOrganizationNumber(countyOrganizationNumber)
                .build();

        consentRequestProducer.publish(kafkaEvidenceRequest);

        instanceRepository.save(Instance.builder()
                .instanceId(kafkaAltinnInstance.getInstanceId())
                .completed(true)
                .fintOrgId(kafkaAltinnInstance.getFintOrgId())
                .build());

        return tuple;
    }
}
