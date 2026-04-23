package no.novari.altinn.altinn;

import no.novari.altinn.altinn.model.*;
import no.novari.fint.altinn.model.kafka.KafkaAltinnInstance;

import java.util.Map;
import java.util.Optional;


public class AltinnInstanceMapper {

    public static final String EMPTY_STRING = "";

    public static KafkaAltinnInstance mapToAltinnInstance(AltinnInstance instanceFromAltinn, AltinnApplicationModel applicationModel) {
        if (applicationModel instanceof DrosjesentralApplicationModel model) {
            return mapToDrosjesentralAltinnInstance(instanceFromAltinn, model);
        } else if (applicationModel instanceof DrosjeloyveApplicationModel model) {
            return mapToDrosjeloyveAltinnInstance(instanceFromAltinn, model);
        }
        throw new IllegalArgumentException("Unknown application model type: " + applicationModel.getClass().getName());
    }

    public static KafkaAltinnInstance mapToDrosjesentralAltinnInstance(AltinnInstance instanceFromAltinn, DrosjesentralApplicationModel applicationModel) {
        return KafkaAltinnInstance.builder()
                .instanceId(instanceFromAltinn.getId())
                .appId(instanceFromAltinn.getAppId())
                .fintOrgId(orgIdMapper(applicationModel.getVirksomhet().getFylke().getFylkesnummer()))

                .organizationNumber(instanceFromAltinn.getInstanceOwner().getOrganisationNumber())
                .organizationName(applicationModel.getVirksomhet().getOrganisasjonsnavn())

                .countyNumber(applicationModel.getVirksomhet().getFylke().getFylkesnummer())
                .countyName(applicationModel.getVirksomhet().getFylke().getFylkesnavn())

                .municipalityNumber(Optional.ofNullable(applicationModel.getVirksomhet())
                        .map(ApplicationVirksomhet::getKommune)
                        .map(ApplicationKommune::getKommunenummer).orElse(EMPTY_STRING))
                .municipalityName(Optional.ofNullable(applicationModel.getVirksomhet())
                        .map(ApplicationVirksomhet::getKommune)
                        .map(ApplicationKommune::getKommunenavn).orElse(EMPTY_STRING))

                .companyEmail(Optional.ofNullable(applicationModel.getVirksomhet())
                        .map(ApplicationVirksomhet::getKontaktinformasjon)
                        .map(ApplicationKontaktinformasjon::getEpostadresse).orElse(EMPTY_STRING))

                .companyPhone(Optional.ofNullable(applicationModel.getVirksomhet())
                        .map(ApplicationVirksomhet::getKontaktinformasjon)
                        .map(ApplicationKontaktinformasjon::getTelefonnummer).orElse(EMPTY_STRING))

                .companyAdressStreet(Optional.ofNullable(applicationModel.getVirksomhet())
                        .map(ApplicationVirksomhet::getForretningsadresse)
                        .map(ApplicationAdresse::getAdresse).orElse(EMPTY_STRING))
                .companyAdressPostcode(Optional.ofNullable(applicationModel.getVirksomhet())
                        .map(ApplicationVirksomhet::getForretningsadresse)
                        .map(ApplicationAdresse::getPostnummer).orElse(EMPTY_STRING))
                .companyAdressPostplace(Optional.ofNullable(applicationModel.getVirksomhet())
                        .map(ApplicationVirksomhet::getForretningsadresse)
                        .map(ApplicationAdresse::getPoststed).orElse(EMPTY_STRING))

                .postalAdressStreet(Optional.ofNullable(applicationModel.getVirksomhet())
                        .map(ApplicationVirksomhet::getPostadresse)
                        .map(ApplicationAdresse::getAdresse).orElse(EMPTY_STRING))
                .postalAdressPostcode(Optional.ofNullable(applicationModel.getVirksomhet())
                        .map(ApplicationVirksomhet::getPostadresse)
                        .map(ApplicationAdresse::getPostnummer).orElse(EMPTY_STRING))
                .postalAdressPostplace(Optional.ofNullable(applicationModel.getVirksomhet())
                        .map(ApplicationVirksomhet::getPostadresse)
                        .map(ApplicationAdresse::getPoststed).orElse(EMPTY_STRING))

                .managerSocialSecurityNumber(applicationModel.getDagligLeder().getFodselsnummer())
                .managerFirstName(applicationModel.getDagligLeder().getFornavn())
                .managerLastName(applicationModel.getDagligLeder().getEtternavn())

                .managerEmail(Optional.ofNullable(applicationModel.getDagligLeder())
                        .map(ApplicationDagligLeder::getKontaktinformasjon)
                        .map(ApplicationKontaktinformasjon::getEpostadresse).orElse(EMPTY_STRING))
                .managerPhone(Optional.ofNullable(applicationModel.getDagligLeder())
                        .map(ApplicationDagligLeder::getKontaktinformasjon)
                        .map(ApplicationKontaktinformasjon::getTelefonnummer).orElse(EMPTY_STRING))

                .build();
    }

    public static KafkaAltinnInstance mapToDrosjeloyveAltinnInstance(AltinnInstance instanceFromAltinn, DrosjeloyveApplicationModel applicationModel) {
        return KafkaAltinnInstance.builder()
                .instanceId(instanceFromAltinn.getId())
                .appId(instanceFromAltinn.getAppId())
                .fintOrgId(orgIdMapper(applicationModel.getVirksomhet().getFylke().getFylkesnummer()))

                .organizationNumber(instanceFromAltinn.getInstanceOwner().getOrganisationNumber())
                .organizationName(applicationModel.getVirksomhet().getOrganisasjonsnavn())

                .countyNumber(applicationModel.getVirksomhet().getFylke().getFylkesnummer())
                .countyName(applicationModel.getVirksomhet().getFylke().getFylkesnavn())

                .municipalityNumber(Optional.ofNullable(applicationModel.getVirksomhet())
                        .map(ApplicationVirksomhet::getKommune)
                        .map(ApplicationKommune::getKommunenummer).orElse(EMPTY_STRING))
                .municipalityName(Optional.ofNullable(applicationModel.getVirksomhet())
                        .map(ApplicationVirksomhet::getKommune)
                        .map(ApplicationKommune::getKommunenavn).orElse(EMPTY_STRING))

                .companyEmail(Optional.ofNullable(applicationModel.getVirksomhet())
                        .map(ApplicationVirksomhet::getKontaktinformasjon)
                        .map(ApplicationKontaktinformasjon::getEpostadresse).orElse(EMPTY_STRING))

                .companyPhone(Optional.ofNullable(applicationModel.getVirksomhet())
                        .map(ApplicationVirksomhet::getKontaktinformasjon)
                        .map(ApplicationKontaktinformasjon::getTelefonnummer).orElse(EMPTY_STRING))

                .companyAdressStreet(Optional.ofNullable(applicationModel.getVirksomhet())
                        .map(ApplicationVirksomhet::getForretningsadresse)
                        .map(ApplicationAdresse::getAdresse).orElse(EMPTY_STRING))
                .companyAdressPostcode(Optional.ofNullable(applicationModel.getVirksomhet())
                        .map(ApplicationVirksomhet::getForretningsadresse)
                        .map(ApplicationAdresse::getPostnummer).orElse(EMPTY_STRING))
                .companyAdressPostplace(Optional.ofNullable(applicationModel.getVirksomhet())
                        .map(ApplicationVirksomhet::getForretningsadresse)
                        .map(ApplicationAdresse::getPoststed).orElse(EMPTY_STRING))

                .postalAdressStreet(Optional.ofNullable(applicationModel.getVirksomhet())
                        .map(ApplicationVirksomhet::getPostadresse)
                        .map(ApplicationAdresse::getAdresse).orElse(EMPTY_STRING))
                .postalAdressPostcode(Optional.ofNullable(applicationModel.getVirksomhet())
                        .map(ApplicationVirksomhet::getPostadresse)
                        .map(ApplicationAdresse::getPostnummer).orElse(EMPTY_STRING))
                .postalAdressPostplace(Optional.ofNullable(applicationModel.getVirksomhet())
                        .map(ApplicationVirksomhet::getPostadresse)
                        .map(ApplicationAdresse::getPoststed).orElse(EMPTY_STRING))

                .managerSocialSecurityNumber(applicationModel.getDagligLeder().getFodselsnummer())
                .managerFirstName(applicationModel.getDagligLeder().getFornavn())
                .managerLastName(applicationModel.getDagligLeder().getEtternavn())

                .managerEmail(Optional.ofNullable(applicationModel.getDagligLeder())
                        .map(ApplicationDagligLeder::getKontaktinformasjon)
                        .map(ApplicationKontaktinformasjon::getEpostadresse).orElse(EMPTY_STRING))
                .managerPhone(Optional.ofNullable(applicationModel.getDagligLeder())
                        .map(ApplicationDagligLeder::getKontaktinformasjon)
                        .map(ApplicationKontaktinformasjon::getTelefonnummer).orElse(EMPTY_STRING))

                .transportmanagerSocialSecurityNumber(Optional.ofNullable(applicationModel.getTransportLeder())
                        .map(ApplicationTransportleder::getFodselsnummer).orElse(EMPTY_STRING))
                .transportmanagerFirstName(Optional.ofNullable(applicationModel.getTransportLeder())
                        .map(ApplicationTransportleder::getFornavn).orElse(EMPTY_STRING))
                .transportmanagerLastName(Optional.ofNullable(applicationModel.getTransportLeder())
                        .map(ApplicationTransportleder::getEtternavn).orElse(EMPTY_STRING))
                .transportmanagerEmail(Optional.ofNullable(applicationModel.getTransportLeder())
                        .map(ApplicationTransportleder::getKontaktinformasjon)
                        .map(ApplicationKontaktinformasjon::getEpostadresse).orElse(EMPTY_STRING))
                .transportmanagerPhone(Optional.ofNullable(applicationModel.getTransportLeder())
                        .map(ApplicationTransportleder::getKontaktinformasjon)
                        .map(ApplicationKontaktinformasjon::getTelefonnummer).orElse(EMPTY_STRING))
                .transportmanagerAffiliation(Optional.ofNullable(applicationModel.getTransportLeder())
                        .map(ApplicationTransportleder::getTilknytning).orElse(EMPTY_STRING))

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
