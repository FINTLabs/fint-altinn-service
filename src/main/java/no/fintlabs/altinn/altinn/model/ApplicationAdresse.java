package no.fintlabs.altinn.altinn.model;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.Getter;

@Getter
@XmlAccessorType(XmlAccessType.FIELD)
public class ApplicationAdresse {
    @XmlElement
    private String adresselinje1;
    
    @XmlElement
    private String adresselinje2;
    
    @XmlElement
    private String postnummer;
    
    @XmlElement
    private String poststed;
}
