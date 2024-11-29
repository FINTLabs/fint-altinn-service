package no.fintlabs.altinn.kafka;


import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class KafkaAltinnInstance {
        private String instanceId;
        private String organizationNumber;
        private String countyCode;
        private String countyName;
}
