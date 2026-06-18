package no.novari.altinn.util;

public final class FilenameSanitizer {

    private FilenameSanitizer() {
    }

    public static String sanitize(String fileName) {
        if (fileName == null) {
            return null;
        }

        return fileName.replaceAll("[^A-Za-z0-9øæå _.-]", "");
    }

    public static String sanitizeOrEmpty(String fileName) {
        String sanitized = sanitize(fileName);
        return sanitized == null ? "" : sanitized;
    }
}
