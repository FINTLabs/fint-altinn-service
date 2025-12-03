package no.novari.altinn.altinn.model;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

@Builder
@Getter
@Jacksonized
public class AltinnInstanceOwner {
    private String partyId;
    private String organisationNumber;
}
