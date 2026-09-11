package software.bananen.gavel.backend.services.analysis;

public class SimpleLanguageDetector implements LanguageDetector {

    private static final java.util.Map<String, String> EXTENSION_MAP = java.util.Map.ofEntries(
            java.util.Map.entry(".java", "Java"),
            java.util.Map.entry(".py", "Python"),
            java.util.Map.entry(".js", "JavaScript"),
            java.util.Map.entry(".ts", "TypeScript"),
            java.util.Map.entry(".cpp", "C++"),
            java.util.Map.entry(".c", "C"),
            java.util.Map.entry(".cs", "C#"),
            java.util.Map.entry(".go", "Go"),
            java.util.Map.entry(".rs", "Rust"),
            java.util.Map.entry(".rb", "Ruby"),
            java.util.Map.entry(".php", "PHP"),
            java.util.Map.entry(".kt", "Kotlin"),
            java.util.Map.entry(".scala", "Scala"),
            java.util.Map.entry(".sh", "Shell"),
            java.util.Map.entry(".yml", "YAML"),
            java.util.Map.entry(".yaml", "YAML"),
            java.util.Map.entry(".json", "JSON"),
            java.util.Map.entry(".xml", "XML"),
            java.util.Map.entry(".html", "HTML"),
            java.util.Map.entry(".css", "CSS")
    );

    /**
     * {@inheritDoc}
     */
    @Override
    public java.util.Optional<String> detectLanguage(String filename) {
        final String extension = getFileExtension(filename).toLowerCase();
        return java.util.Optional.ofNullable(EXTENSION_MAP.get(extension));
    }

    private String getFileExtension(String filename) {
        final int lastDot = filename.lastIndexOf('.');
        return lastDot > 0 ? filename.substring(lastDot) : "";
    }
}
