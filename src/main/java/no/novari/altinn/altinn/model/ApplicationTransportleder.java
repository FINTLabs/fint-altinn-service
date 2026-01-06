package no.novari.altinn.altinn.model;

import jakarta.xml.bind.annotation.XmlElement;

public class ApplicationTransportleder {

    @XmlElement
    private String fodselsnummer;

    @XmlElement
    private String fornavn;

    @XmlElement
    private String etternavn;

    @XmlElement
    private ApplicationKontaktinformasjon kontaktinformasjon;

}
