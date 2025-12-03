package no.novari.altinn.altinn.model;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

import java.time.Instant;

@Builder
@Getter
@Jacksonized
public class AltinnProcess {
    private Instant started;
    private String startEvent;
    private Instant ended;
    private String endEvent;
}
