package no.novari.altinn.altinn.model;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.Getter;

@Getter
@XmlAccessorType(XmlAccessType.FIELD)
public class ApplicationFylke {
    @XmlElement
    private String fylkesnummer;
    
    @XmlElement
    private String fylkesnavn;
}
