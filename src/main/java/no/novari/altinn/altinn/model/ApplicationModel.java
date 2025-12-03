package no.novari.altinn.altinn.model;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;

@XmlRootElement(name = "Datamodell")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class ApplicationModel {
    @XmlElement
    private ApplicationVirksomhet virksomhet;
    
    @XmlElement
    private ApplicationDagligLeder dagligLeder;
    
    @XmlElement
    private boolean bekreftelseOkonomiOgVandel;
    
    @XmlElement
    private boolean bekreftelseKjentMedSentralensPlikter;
    
    @XmlElement
    private boolean bekreftelseSentralePlikter9d;
    
    @XmlElement
    private String virksomhetFlereFylker;
}
