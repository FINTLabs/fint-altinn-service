package no.fintlabs.altinn.altinn.model;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.Getter;

@Getter
@XmlAccessorType(XmlAccessType.FIELD)
public class ApplicationVirksomhet {
    @XmlElement
    private String organisasjonsnummer;
    
    @XmlElement
    private String organisasjonsnavn;
    
    @XmlElement
    private String organisasjonsform;
    
    @XmlElement
    private ApplicationAdresse forretningsadresse;
    
    @XmlElement
    private ApplicationAdresse postadresse;
    
    @XmlElement
    private ApplicationFylke fylke;
    
    @XmlElement
    private ApplicationKommune kommune;
    
    @XmlElement
    private ApplicationKontaktinformasjon kontaktinformasjon;
}
