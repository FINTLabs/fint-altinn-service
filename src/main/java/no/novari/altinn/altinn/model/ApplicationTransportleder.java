package no.novari.altinn.altinn.model;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.Getter;

@Getter
@XmlAccessorType(XmlAccessType.FIELD)
public class ApplicationTransportleder {

    @XmlElement
    private String fodselsnummer;

    @XmlElement
    private String fornavn;

    @XmlElement
    private String etternavn;

    @XmlElement
    private ApplicationKontaktinformasjon kontaktinformasjon;

    @XmlElement
    private String tilknytning;
}
