package software.bananen.gavel.behavioralanalysis.git;

import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * A service that generates hotspot metrics based on the git version control
 * system.
 */
public final class GitService {

    /**
     * Loads the git repository from the given project directory.
     * <p>
     * This will assume that the project directory contains a .git directory.
     *
     * @param projectDirectory The project directory.
     * @return The loaded repository.
     * @throws IOException Might be thrown in case that loading the repository
     *                     fails.
     */
    public Repository loadRepository(final Path projectDirectory) throws IOException {
        return new FileRepositoryBuilder()
                .setGitDir(projectDirectory.resolve(".git").toAbsolutePath().toFile())
                .build();
    }

    /**
     * Locates all the git repositories in the given paths.
     *
     * @param paths The paths that the repositories should be located in.
     * @return The located git repositories.
     */
    public Collection<Path> locateGitRepositories(final Collection<String> paths) {
        return paths.stream()
                .map(Paths::get)
                .flatMap(path -> locateGitRepositories(path).stream())
                .map(Path::toAbsolutePath)
                .collect(Collectors.toSet());
    }

    /**
     * Searches through the given directory to find the .git directories that
     * indicate the location of a git repository.
     *
     * @param directory The directory.
     * @return The .git directories.
     */
    public Collection<Path> findDotGitDirectories(final Path directory) {
        final Collection<Path> dotGitDirectories = new ArrayList<>();

        for (final File subdirectory :
                Optional.ofNullable(directory.toFile().listFiles(File::isDirectory))
                        .orElse(new File[0])) {
            if (subdirectory.getName().endsWith(".git")) {
                dotGitDirectories.add(subdirectory.toPath());
            } else {
                dotGitDirectories.addAll(findDotGitDirectories(subdirectory.toPath()));
            }
        }

        return dotGitDirectories;
    }

    /**
     * Locates the git repositories in the given root path and its subdirectories.
     *
     * @param rootPath The root path that the git repositories should be located in.
     * @return The located git repositories.
     */
    public Collection<Path> locateGitRepositories(final Path rootPath) {
        return findDotGitDirectories(rootPath)
                .stream()
                .filter(directory -> directory.endsWith(".git"))
                .map(this::extractProjectDirectory)
                .map(directory -> {
                    // No clue why we have to prepend a /
                    if (!directory.startsWith("/")) {
                        return Paths.get("/" + directory);
                    } else {
                        return directory;
                    }
                })
                .collect(Collectors.toSet());
    }

    /**
     * Extracts the project directory from the repositories' path.
     *
     * @param gitDirectory The git directory.
     * @return The project directory.
     */
    private Path extractProjectDirectory(final Path gitDirectory) {
        return gitDirectory.subpath(0, gitDirectory.getNameCount() - 1);
    }
}
