package no.novari.altinn.altinn.model;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.Data;

@XmlAccessorType(XmlAccessType.FIELD)
@Data
public abstract class AltinnApplicationModel {

    @XmlElement
    private ApplicationVirksomhet virksomhet;

    @XmlElement
    private ApplicationDagligLeder dagligLeder;
}
