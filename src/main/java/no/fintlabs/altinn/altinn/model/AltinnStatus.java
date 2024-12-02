package no.fintlabs.altinn.altinn.model;

import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

import java.time.Instant;

@Builder
@Jacksonized
public class AltinnStatus {
    private boolean isArchived;
    private Instant archived;
    private boolean isSoftDeleted;
    private boolean isHardDeleted;
    private String readStatus;
}
