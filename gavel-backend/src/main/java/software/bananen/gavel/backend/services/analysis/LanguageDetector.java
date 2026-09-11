package software.bananen.gavel.backend.services.analysis;

public interface LanguageDetector {
    java.util.Optional<String> detectLanguage(String filename);
}
