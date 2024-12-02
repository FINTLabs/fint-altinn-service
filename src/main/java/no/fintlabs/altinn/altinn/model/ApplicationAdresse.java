package no.fintlabs.altinn.altinn.model;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.Getter;

@Getter
@XmlAccessorType(XmlAccessType.FIELD)
public class ApplicationAdresse {
    @XmlElement
    private String adresse;
    
    @XmlElement
    private String postnummer;
    
    @XmlElement
    private String poststed;
}