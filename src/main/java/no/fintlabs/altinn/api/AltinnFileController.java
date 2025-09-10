package no.fintlabs.altinn.api;

import lombok.extern.slf4j.Slf4j;
import no.fintlabs.altinn.database.InstanceFile;
import no.fintlabs.altinn.database.InstanceRepository;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/file")
public class AltinnFileController {

    private final InstanceRepository instanceRepository;
    private final WebClient webClient;
    private final WebClient ebevisWebClient;

    public AltinnFileController(InstanceRepository altinnRepository, WebClient altinnWebClient, WebClient ebevisWebClient) {
        this.instanceRepository = altinnRepository;
        this.webClient = altinnWebClient;
        this.ebevisWebClient = ebevisWebClient;
    }

    @GetMapping("/{partyId}/{instanceId}")
    public List<String> getFileList(@PathVariable String partyId, @PathVariable String instanceId) {
        String id = partyId.concat("/").concat(instanceId);
        log.info("Getting file(s) for instanceId: {}", id);

        return instanceRepository.findFirstByInstanceIdOrderByLastUpdatedDesc(id)
                .getFiles().stream()
                .map(InstanceFile::getDataType)
                .collect(Collectors.toList());
    }

    @GetMapping("/{partyId}/{instanceId}/{fileDataType}")
    public Mono<ResponseEntity<ByteArrayResource>> getFileContent(@PathVariable String partyId,
                                                                  @PathVariable String instanceId,
                                                                  @PathVariable String fileDataType) {
        String partyInstanceId = partyId.concat("/").concat(instanceId);
        log.info("Getting file for instanceId: {} and dataType: {}", partyInstanceId, fileDataType);

        return instanceRepository.findFirstByInstanceIdOrderByLastUpdatedDesc(partyInstanceId).getFiles().stream()
                .filter(file -> file.getDataType().equals(fileDataType))
                .findFirst()
                .map(this::retrieveFile)
                .orElseGet(() -> Mono.just(ResponseEntity.notFound().build()));
    }

    @GetMapping("/ebevis/{partyId}/{instanceId}/{evidenceCodeName}")
    public Mono<ResponseEntity<ByteArrayResource>> getEbevisFileContent(@PathVariable String partyId,
                                                                        @PathVariable String instanceId,
                                                                        @PathVariable String evidenceCodeName) {

        log.info("Getting file for instanceId: {} and dataType: {}", instanceId, evidenceCodeName);

        return ebevisWebClient.get()
                .uri(String.format("evidence/file/%s/%s/%s", partyId, instanceId, evidenceCodeName))
                .retrieve()
                .bodyToMono(ByteArrayResource.class)
                .map(resource -> ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_PDF)
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=evidence.pdf")
                        .body(resource));
    }

    private Mono<ResponseEntity<ByteArrayResource>> retrieveFile(InstanceFile file) {
        return webClient.get()
                .uri(file.getUrl())
                .retrieve()
                .bodyToMono(ByteArrayResource.class)
                .map(resource -> ResponseEntity.ok()
                        .contentType(MediaType.valueOf(file.getContentType()))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + file.getFileName())
                        .body(resource))
                .onErrorResume(e -> {
                            log.error("Error fetching file", e);
                            return Mono.just(ResponseEntity.internalServerError().build());
                        }
                );
    }
}
