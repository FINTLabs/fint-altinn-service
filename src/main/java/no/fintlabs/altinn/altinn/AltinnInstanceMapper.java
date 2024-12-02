package no.fintlabs.altinn.altinn;

import no.fint.altinn.model.kafka.KafkaAltinnInstance;
import no.fintlabs.altinn.altinn.model.*;

import javax.swing.text.html.Option;
import java.util.Optional;


public class AltinnInstanceMapper {

    public static KafkaAltinnInstance mapToAltinnInstance(AltinnInstance instanceFromAltinn, ApplicationModel applicationModel) {
        return KafkaAltinnInstance.builder()
                .instanceId(instanceFromAltinn.getId())

                .organizationNumber(instanceFromAltinn.getInstanceOwner().getOrganisationNumber())
                .organizationName(applicationModel.getVirksomhet().getOrganisasjonsnavn())

                .countyNumber(applicationModel.getVirksomhet().getFylke().getFylkesnummer())
                .countyName(applicationModel.getVirksomhet().getFylke().getFylkesnavn())

                .companyEmail(Optional.ofNullable(applicationModel.getVirksomhet())
                        .map(ApplicationVirksomhet::getKontaktinformasjon)
                        .map(ApplicationKontaktinformasjon::getEpostadresse).orElse(null))

                .companyPhone(Optional.ofNullable(applicationModel.getVirksomhet())
                        .map(ApplicationVirksomhet::getKontaktinformasjon)
                        .map(ApplicationKontaktinformasjon::getTelefonnummer).orElse(null))

                .companyAdressStreet(Optional.ofNullable(applicationModel.getVirksomhet())
                        .map(ApplicationVirksomhet::getForretningsadresse)
                        .map(ApplicationAdresse::getAdresse).orElse(null))
                .companyAdressPostcode(Optional.ofNullable(applicationModel.getVirksomhet())
                        .map(ApplicationVirksomhet::getForretningsadresse)
                        .map(ApplicationAdresse::getPostnummer).orElse(null))
                .companyAdressPostplace(Optional.ofNullable(applicationModel.getVirksomhet())
                        .map(ApplicationVirksomhet::getForretningsadresse)
                        .map(ApplicationAdresse::getPoststed).orElse(null))

                .postalAdressStreet(Optional.ofNullable(applicationModel.getVirksomhet())
                        .map(ApplicationVirksomhet::getPostadresse)
                        .map(ApplicationAdresse::getAdresse).orElse(null))
                .postalAdressPostcode(Optional.ofNullable(applicationModel.getVirksomhet())
                        .map(ApplicationVirksomhet::getPostadresse)
                        .map(ApplicationAdresse::getPostnummer).orElse(null))
                .postalAdressPostplace(Optional.ofNullable(applicationModel.getVirksomhet())
                        .map(ApplicationVirksomhet::getPostadresse)
                        .map(ApplicationAdresse::getPoststed).orElse(null))

                .managerSocialSecurityNumber(applicationModel.getDagligLeder().getFodselsnummer())
                .managerFirstName(applicationModel.getDagligLeder().getFornavn())
                .managerLastName(applicationModel.getDagligLeder().getEtternavn())
                
                .managerEmail(Optional.ofNullable(applicationModel.getDagligLeder())
                        .map(ApplicationDagligLeder::getKontaktinformasjon)
                        .map(ApplicationKontaktinformasjon::getEpostadresse).orElse(null))
                .managerPhone(Optional.ofNullable(applicationModel.getDagligLeder())
                        .map(ApplicationDagligLeder::getKontaktinformasjon)
                        .map(ApplicationKontaktinformasjon::getTelefonnummer).orElse(null))

                .build();
    }
}
