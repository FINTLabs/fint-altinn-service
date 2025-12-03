package no.novari.altinn.altinn;

import no.fint.altinn.model.kafka.KafkaAltinnInstance;
import no.novari.altinn.altinn.model.*;

import java.util.Map;
import java.util.Optional;


public class AltinnInstanceMapper {

    public static final String EMTPY_STRING = "";

    public static KafkaAltinnInstance mapToAltinnInstance(AltinnInstance instanceFromAltinn, ApplicationModel applicationModel) {
        return KafkaAltinnInstance.builder()
                .instanceId(instanceFromAltinn.getId())
                .fintOrgId(orgIdMapper(applicationModel.getVirksomhet().getFylke().getFylkesnummer()))

                .organizationNumber(instanceFromAltinn.getInstanceOwner().getOrganisationNumber())
                .organizationName(applicationModel.getVirksomhet().getOrganisasjonsnavn())

                .countyNumber(applicationModel.getVirksomhet().getFylke().getFylkesnummer())
                .countyName(applicationModel.getVirksomhet().getFylke().getFylkesnavn())

                .municipalityNumber(Optional.ofNullable(applicationModel.getVirksomhet())
                        .map(ApplicationVirksomhet::getKommune)
                        .map(ApplicationKommune::getKommunenummer).orElse(EMTPY_STRING))
                .municipalityName(Optional.ofNullable(applicationModel.getVirksomhet())
                        .map(ApplicationVirksomhet::getKommune)
                        .map(ApplicationKommune::getKommunenavn).orElse(EMTPY_STRING))

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

    private static String orgIdMapper(String fylkesnummer) {
        return Map.ofEntries(
                Map.entry("11", "rogfk.no"),
                Map.entry("15", "mrfylke.no"),
                Map.entry("18", "nfk.no"),
                Map.entry("31", "ofk.no"),
                Map.entry("32", "afk.no"),
                Map.entry("33", "bfk.no"),
                Map.entry("34", "innlandetfylke.no"),
                Map.entry("39", "vestfoldfylke.no"),
                Map.entry("40", "telemarkfylke.no"),
                Map.entry("42", "agderfk.no"),
                Map.entry("46", "vlfk.no"),
                Map.entry("50", "trondelagfylke.no"),
                Map.entry("55", "tromsfylke.no"),
                Map.entry("56", "ffk.no"),
                Map.entry("03", "bym.oslo.kommune.no")
        ).getOrDefault(fylkesnummer, "");
    }
}
