package no.fintlabs.altinn.altinn;

import no.fint.altinn.model.kafka.KafkaAltinnInstance;
import no.fintlabs.altinn.altinn.model.*;

import javax.swing.text.html.Option;
import java.util.Optional;


public class AltinnInstanceMapper {

    public static final String EMTPY_STRING = "";

    public static KafkaAltinnInstance mapToAltinnInstance(AltinnInstance instanceFromAltinn, ApplicationModel applicationModel) {
        return KafkaAltinnInstance.builder()
                .instanceId(instanceFromAltinn.getId())

                .organizationNumber(instanceFromAltinn.getInstanceOwner().getOrganisationNumber())
                .organizationName(applicationModel.getVirksomhet().getOrganisasjonsnavn())

                .countyNumber(applicationModel.getVirksomhet().getFylke().getFylkesnummer())
                .countyName(applicationModel.getVirksomhet().getFylke().getFylkesnavn())

                .companyEmail(Optional.ofNullable(applicationModel.getVirksomhet())
                        .map(ApplicationVirksomhet::getKontaktinformasjon)
                        .map(ApplicationKontaktinformasjon::getEpostadresse).orElse(EMTPY_STRING))

                .companyPhone(Optional.ofNullable(applicationModel.getVirksomhet())
                        .map(ApplicationVirksomhet::getKontaktinformasjon)
                        .map(ApplicationKontaktinformasjon::getTelefonnummer).orElse(EMTPY_STRING))

                .companyAdressStreet(Optional.ofNullable(applicationModel.getVirksomhet())
                        .map(ApplicationVirksomhet::getForretningsadresse)
                        .map(ApplicationAdresse::getAdresse).orElse(EMTPY_STRING))
                .companyAdressPostcode(Optional.ofNullable(applicationModel.getVirksomhet())
                        .map(ApplicationVirksomhet::getForretningsadresse)
                        .map(ApplicationAdresse::getPostnummer).orElse(EMTPY_STRING))
                .companyAdressPostplace(Optional.ofNullable(applicationModel.getVirksomhet())
                        .map(ApplicationVirksomhet::getForretningsadresse)
                        .map(ApplicationAdresse::getPoststed).orElse(EMTPY_STRING))

                .postalAdressStreet(Optional.ofNullable(applicationModel.getVirksomhet())
                        .map(ApplicationVirksomhet::getPostadresse)
                        .map(ApplicationAdresse::getAdresse).orElse(EMTPY_STRING))
                .postalAdressPostcode(Optional.ofNullable(applicationModel.getVirksomhet())
                        .map(ApplicationVirksomhet::getPostadresse)
                        .map(ApplicationAdresse::getPostnummer).orElse(EMTPY_STRING))
                .postalAdressPostplace(Optional.ofNullable(applicationModel.getVirksomhet())
                        .map(ApplicationVirksomhet::getPostadresse)
                        .map(ApplicationAdresse::getPoststed).orElse(EMTPY_STRING))

                .managerSocialSecurityNumber(applicationModel.getDagligLeder().getFodselsnummer())
                .managerFirstName(applicationModel.getDagligLeder().getFornavn())
                .managerLastName(applicationModel.getDagligLeder().getEtternavn())

                .managerEmail(Optional.ofNullable(applicationModel.getDagligLeder())
                        .map(ApplicationDagligLeder::getKontaktinformasjon)
                        .map(ApplicationKontaktinformasjon::getEpostadresse).orElse(EMTPY_STRING))
                .managerPhone(Optional.ofNullable(applicationModel.getDagligLeder())
                        .map(ApplicationDagligLeder::getKontaktinformasjon)
                        .map(ApplicationKontaktinformasjon::getTelefonnummer).orElse(EMTPY_STRING))

                .build();
    }
}
