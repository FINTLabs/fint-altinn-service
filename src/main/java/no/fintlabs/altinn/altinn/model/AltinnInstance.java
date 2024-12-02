package no.fintlabs.altinn.altinn.model;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Builder
@Getter
@Jacksonized
public class AltinnInstance {
    private String id;
    private AltinnInstanceOwner instanceOwner;
    private String appId;
    private String org;
    private Map<String, String> selfLinks;
    private Instant visibleAfter;
    private AltinnProcess process;
    private AltinnStatus status;
    private List<AltinnData> data;
    private Instant created;
    private String createdBy;
    private Instant lastChanged;
    private String lastChangedBy;
}

