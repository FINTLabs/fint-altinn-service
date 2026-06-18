package no.novari.altinn.kafka;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;

class EbevisConsentAcceptedConsumerTest {

    private EbevisConsentAcceptedConsumer consumer;
    private Method sanitizeFileName;

    @BeforeEach
    void setUp() throws Exception {
        consumer = new EbevisConsentAcceptedConsumer(null, null, null, null, null);
        sanitizeFileName = EbevisConsentAcceptedConsumer.class.getDeclaredMethod("sanitizeFileName", String.class);
        sanitizeFileName.setAccessible(true);
    }

    @Test
    void shouldRemoveSpecialCharactersAndKeepAllowedCharacters() throws Exception {
        String result = (String) sanitizeFileName.invoke(consumer, "Søknad 2026 (v1).pdf!");

        assertThat(result).isEqualTo("Søknad 2026 v1.pdf");
    }

    @Test
    void shouldRemoveArabicIndicDigitsFromFileName() throws Exception {
        String resultWithArabicIndicDigits = (String) sanitizeFileName.invoke(consumer, "Screenshot_٢٠٢٦٠٦١٢_١٦١٠٣٥_Samsung Notes.jpg");

        assertThat(resultWithArabicIndicDigits).isEqualTo("Screenshot___Samsung Notes.jpg");
    }
}
