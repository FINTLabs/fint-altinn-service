package no.novari.altinn.api;

import lombok.extern.slf4j.Slf4j;
import no.novari.altinn.altinn.AltinnInstanceSheduled;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/event")
public class AlltinnEventController {

    private final AltinnInstanceSheduled altinnInstanceSheduled;

    public AlltinnEventController(AltinnInstanceSheduled altinnInstanceSheduled) {
        this.altinnInstanceSheduled = altinnInstanceSheduled;
    }

    @PostMapping("/push")
    public void webHook() {
        altinnInstanceSheduled.getAltinnInstances();
    }
}
