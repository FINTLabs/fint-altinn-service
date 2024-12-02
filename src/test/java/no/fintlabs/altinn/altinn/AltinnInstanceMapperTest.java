package no.fintlabs.altinn.altinn;

import no.fint.altinn.model.kafka.KafkaAltinnInstance;
import no.fintlabs.altinn.altinn.model.*;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.assertj.core.api.Assertions.assertThat;

class AltinnInstanceMapperTest {

    @Test
    void shouldMapAllFieldsCorrectly() throws Exception {
        AltinnInstanceOwner owner = AltinnInstanceOwner.builder()
                .organisationNumber("123456789")
                .build();

        AltinnInstance altinnInstance = AltinnInstance.builder()
                .id("instance-123")
                .instanceOwner(owner)
                .build();

        ApplicationFylke fylke = new ApplicationFylke();
        setField(fylke, "fylkesnummer", "50");
        setField(fylke, "fylkesnavn", "Trøndelag");

        ApplicationVirksomhet virksomhet = new ApplicationVirksomhet();
        setField(virksomhet, "fylke", fylke);

        ApplicationModel applicationModel = new ApplicationModel();
        setField(applicationModel, "virksomhet", virksomhet);

        KafkaAltinnInstance result = AltinnInstanceMapper.mapToAltinnInstance(altinnInstance, applicationModel);

        assertThat(result.getInstanceId()).isEqualTo("instance-123");
        assertThat(result.getOrganizationNumber()).isEqualTo("123456789");
        assertThat(result.getCountyCode()).isEqualTo("50");
        assertThat(result.getCountyName()).isEqualTo("Trøndelag");
    }

    private void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}
