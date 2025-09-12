package no.fintlabs.altinn.altinn;

import lombok.extern.slf4j.Slf4j;
import no.fint.altinn.model.kafka.KafkaAltinnInstance;
import no.fint.altinn.model.kafka.KafkaEvidenceConsentRequest;
import no.fintlabs.altinn.altinn.model.AltinnInstance;
import no.fintlabs.altinn.altinn.model.ApplicationModel;
import no.fintlabs.altinn.database.Instance;
import no.fintlabs.altinn.database.InstanceRepository;
import no.fintlabs.altinn.kafka.EbevisConsentRequestProducer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import static no.fintlabs.altinn.altinn.AltinnInstanceMapper.mapToAltinnInstance;

@Slf4j
@Component
public class AltinnInstanceSheduled {

    @Value("${fint.county-number}")
    private String countyNumber;

    @Value("${fint.org-id}")
    private String orgId;

    @Value("${fint.county-organization-number}")
    private String countyOrganizationNumber;

    private final AltinnInstanceService altinnInstanceService;
    private final InstanceRepository instanceRepository;
    private final EbevisConsentRequestProducer consentRequestProducer;

    public AltinnInstanceSheduled(AltinnInstanceService altinnInstanceService, InstanceRepository instanceRepository,
                                  EbevisConsentRequestProducer consentRequestProducer) {
        this.altinnInstanceService = altinnInstanceService;
        this.instanceRepository = instanceRepository;
        this.consentRequestProducer = consentRequestProducer;
    }

    @Scheduled(cron = "0 */10 * * * ?")
    public void getAltinnInstances() {
        log.info("⏱️ Scheduled fetching of altinn instances");

        altinnInstanceService.getInstances()
                .flatMapMany(Flux::fromIterable)
                .filter(this::isNew)
                .flatMap(this::requestApplicationData)
                .filter(this::onlyInstancesForConfiguredCounty)
                .doOnNext(this::publishEbevisConcentRequest)
                .subscribe();
    }

    private boolean isNew(AltinnInstance altinnInstanse) {
        return instanceRepository.findAllInstances().stream()
                .noneMatch(instance -> instance.getInstanceId().equals(altinnInstanse.getId()));
    }

    private Mono<Tuple2<AltinnInstance, ApplicationModel>> requestApplicationData(AltinnInstance altinnInstance) {
        return Mono.zip(Mono.just(altinnInstance), altinnInstanceService.getApplicationData(altinnInstance));
    }

    private boolean onlyInstancesForConfiguredCounty(Tuple2<AltinnInstance, ApplicationModel> tuple2) {
        log.debug("CountyNumber: {}, County in applicationData: {}", countyNumber,
                tuple2.getT2().getVirksomhet().getFylke().getFylkesnummer());

        return tuple2.getT2().getVirksomhet().getFylke().getFylkesnummer().equals(countyNumber);
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

        instanceRepository.save(Instance.builder()
                .instanceId(kafkaAltinnInstance.getInstanceId())
                .completed(true)
                .fintOrgId(kafkaAltinnInstance.getFintOrgId())
                .build());

        return tuple;
    }
}
