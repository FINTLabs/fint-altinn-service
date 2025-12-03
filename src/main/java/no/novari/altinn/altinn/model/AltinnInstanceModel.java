package no.novari.altinn.altinn.model;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@Builder
@Getter
@Jacksonized
public class AltinnInstanceModel {
    private int count;
    private String self;
    private String next;
    private List<AltinnInstance> instances;
}
