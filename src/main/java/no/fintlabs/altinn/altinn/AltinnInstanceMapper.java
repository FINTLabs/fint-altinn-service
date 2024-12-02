package no.fintlabs.altinn.altinn;

import no.fint.altinn.model.kafka.KafkaAltinnInstance;
import no.fintlabs.altinn.altinn.model.AltinnInstance;
import no.fintlabs.altinn.altinn.model.ApplicationModel;


public class AltinnInstanceMapper {

    public static KafkaAltinnInstance mapToAltinnInstance(AltinnInstance instanceFromAltinn, ApplicationModel t2) {
        return KafkaAltinnInstance.builder()
                .instanceId(instanceFromAltinn.getId())
                .organizationNumber(instanceFromAltinn.getInstanceOwner().getOrganisationNumber())
                .countyCode(t2.getVirksomhet().getFylke().getFylkesnummer())
                .countyName(t2.getVirksomhet().getFylke().getFylkesnavn())
                .build();
    }
}
