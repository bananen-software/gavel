package software.bananen.gavel.behavioralanalysis.git;

import org.junit.jupiter.api.Test;
import software.bananen.gavel.domain.model.Commit;
import software.bananen.gavel.domain.model.FileDiff;
import software.bananen.gavel.domain.ports.driven.VersionControlRepository;

import java.nio.file.Paths;

class GitVersionControlSystemAdapterTest {

    @Test
    void testy() throws Throwable {
        final var adapter = new GitVersionControlSystemAdapter();

        for (final VersionControlRepository repository : adapter.findRepositoriesIn(Paths.get("/home/dennis/workspace/github/bdd4j/"))) {
            for (final Commit commit : repository.commits()) {
                System.out.println(commit.timestamp() + ": " + commit.shortMessage() + " | " + commit.author().asString());
                System.out.println("---------------------------------------------------");

                for (final FileDiff diff : commit.diffs()) {
                    System.out.println("\tType: " + diff.type());
                    System.out.println("\tNew: " + diff.newPath());
                    System.out.println("\tOld: " + diff.oldPath());
                    System.out.println();
                }

                System.out.println("---------------------------------------------------");

                System.out.println();
            }
        }
    }
}