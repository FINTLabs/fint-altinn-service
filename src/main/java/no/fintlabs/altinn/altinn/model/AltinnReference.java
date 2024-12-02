package no.fintlabs.altinn.altinn.model;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

@Builder
@Getter
@Jacksonized
public class AltinnReference {
    private String value;
    private String relation;
    private String valueType;
}
