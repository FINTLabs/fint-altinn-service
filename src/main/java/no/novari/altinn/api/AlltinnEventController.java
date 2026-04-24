package no.novari.altinn.api;

import lombok.extern.slf4j.Slf4j;
import no.novari.altinn.altinn.AltinnInstanceScheduled;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/event")
public class AlltinnEventController {

    private final AltinnInstanceScheduled altinnInstanceScheduled;

    public AlltinnEventController(AltinnInstanceScheduled altinnInstanceScheduled) {
        this.altinnInstanceScheduled = altinnInstanceScheduled;
    }

    @PostMapping("/push")
    public void webHook() {
        altinnInstanceScheduled.getAltinnInstances();
    }
}
