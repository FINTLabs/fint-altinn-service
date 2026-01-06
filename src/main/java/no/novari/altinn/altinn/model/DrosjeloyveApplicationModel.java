package no.novari.altinn.altinn.model;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;

@XmlRootElement(name = "Datamodell")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class DrosjeloyveApplicationModel extends AltinnApplicationModel {

    @XmlElement
    private int antallDrosjeloyver;

    @XmlElement
    private boolean bekreftelseMeldingOmSamtykke;

    @XmlElement
    private boolean bekreftelseOkonomiskGaranti;

    @XmlElement
    private boolean annenTransportleder;

    @XmlElement
    private ApplicationTransportleder transportLeder;

    @XmlElement
    private boolean bekreftelseTransportleder;

    @XmlElement
    private String forklaringTransportleder;
}
