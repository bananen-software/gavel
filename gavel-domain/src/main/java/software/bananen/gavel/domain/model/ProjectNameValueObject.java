package software.bananen.gavel.domain.model;

import java.nio.file.Path;

public record ProjectNameValueObject(String value) {

    public static ProjectNameValueObject fromPath(final Path projectPath) {
        return new ProjectNameValueObject(
                projectPath.getName(projectPath.getNameCount() - 1).toString());
    }
}
