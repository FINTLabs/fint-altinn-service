package no.novari.altinn.altinn.model;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Builder
@Getter
@Jacksonized
public class AltinnData {
    private String id;
    private String instanceGuid;
    private String dataType;
    private String contentType;
    private String filename;
    private String blobStoragePath;
    private Map<String, String> selfLinks;
    private long size;
    private boolean locked;
    private boolean isRead;
    private List<String> tags;
    private String fileScanResult;
    private List<AltinnReference> references;
    private Instant created;
    private String createdBy;
    private Instant lastChanged;
    private String lastChangedBy;
}
