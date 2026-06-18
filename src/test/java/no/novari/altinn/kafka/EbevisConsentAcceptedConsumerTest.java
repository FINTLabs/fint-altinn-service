package no.novari.altinn.kafka;

import no.novari.altinn.util.FilenameSanitizer;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EbevisConsentAcceptedConsumerTest {

    @Test
    void shouldRemoveSpecialCharactersAndKeepAllowedCharacters() {
        String result = FilenameSanitizer.sanitize("Søknad 2026 (v1).pdf!");

        assertThat(result).isEqualTo("Søknad 2026 v1.pdf");
    }

    @Test
    void shouldRemoveArabicIndicDigitsFromFileName() {
        String resultWithArabicIndicDigits = FilenameSanitizer.sanitize("Screenshot_٢٠٢٦٠٦١٢_١٦١٠٣٥_Samsung Notes.jpg");

        assertThat(resultWithArabicIndicDigits).isEqualTo("Screenshot___Samsung Notes.jpg");
    }
}
