package no.novari.altinn.altinn.model;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;
import lombok.EqualsAndHashCode;

@XmlRootElement(name = "Datamodell")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
@EqualsAndHashCode(callSuper=false)
public class DrosjesentralApplicationModel extends AltinnApplicationModel {
    
    @XmlElement
    private boolean bekreftelseOkonomiOgVandel;
    
    @XmlElement
    private boolean bekreftelseKjentMedSentralensPlikter;
    
    @XmlElement
    private boolean bekreftelseSentralePlikter9d;
    
    @XmlElement
    private String virksomhetFlereFylker;
}
