package no.novari.altinn.altinn;

import no.fint.altinn.model.kafka.KafkaAltinnInstance;
import no.novari.altinn.altinn.model.*;
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

        DrosjesentralApplicationModel applicationModel = new DrosjesentralApplicationModel();
        setField(applicationModel, "virksomhet", virksomhet);

        ApplicationDagligLeder dagligLeder = new ApplicationDagligLeder();
        setField(dagligLeder, "fodselsnummer", "12345678901");
        setField(dagligLeder, "fornavn", "Ola");
        setField(dagligLeder, "etternavn", "Nordmann");
        setField(applicationModel, "dagligLeder", dagligLeder);

        KafkaAltinnInstance result = AltinnInstanceMapper.mapToDrosjesentralAltinnInstance(altinnInstance, applicationModel);

        assertThat(result.getInstanceId()).isEqualTo("instance-123");
        assertThat(result.getOrganizationNumber()).isEqualTo("123456789");
        assertThat(result.getCountyNumber()).isEqualTo("50");
        assertThat(result.getCountyName()).isEqualTo("Trøndelag");
    }

    @Test
    void shouldMapAllDrosjeloyveFieldsCorrectly() throws Exception {
        AltinnInstanceOwner owner = AltinnInstanceOwner.builder()
                .organisationNumber("123456789")
                .build();

        AltinnInstance altinnInstance = AltinnInstance.builder()
                .id("instance-123")
                .appId("vigo/drosjeloyve")
                .instanceOwner(owner)
                .build();

        ApplicationFylke fylke = new ApplicationFylke();
        setField(fylke, "fylkesnummer", "50");
        setField(fylke, "fylkesnavn", "Trøndelag");

        ApplicationVirksomhet virksomhet = new ApplicationVirksomhet();
        setField(virksomhet, "fylke", fylke);

        DrosjeloyveApplicationModel applicationModel = new DrosjeloyveApplicationModel();
        setField(applicationModel, "virksomhet", virksomhet);

        ApplicationDagligLeder dagligLeder = new ApplicationDagligLeder();
        setField(dagligLeder, "fodselsnummer", "12345678901");
        setField(dagligLeder, "fornavn", "Ola");
        setField(dagligLeder, "etternavn", "Nordmann");
        setField(applicationModel, "dagligLeder", dagligLeder);

        ApplicationTransportleder transportleder = new ApplicationTransportleder();
        setField(transportleder, "fodselsnummer", "12345678901");
        setField(transportleder, "fornavn", "Kari");
        setField(transportleder, "etternavn", "Nordmann");
        setField(applicationModel, "transportLeder", transportleder);

        KafkaAltinnInstance result = AltinnInstanceMapper.mapToDrosjeloyveAltinnInstance(altinnInstance, applicationModel);

        assertThat(result.getInstanceId()).isEqualTo("instance-123");
        assertThat(result.getOrganizationNumber()).isEqualTo("123456789");
        assertThat(result.getCountyNumber()).isEqualTo("50");
        assertThat(result.getCountyName()).isEqualTo("Trøndelag");
        assertThat(result.getAppId()).isEqualTo("vigo/drosjeloyve");
        assertThat(result.getTransportmanagerFirstName()).isEqualTo("Kari");
    }

    private void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = findField(target.getClass(), fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    private Field findField(Class<?> clazz, String fieldName) throws NoSuchFieldException {
        while (clazz != null) {
            try {
                return clazz.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass();
            }
        }
        throw new NoSuchFieldException(fieldName);
    }
}
