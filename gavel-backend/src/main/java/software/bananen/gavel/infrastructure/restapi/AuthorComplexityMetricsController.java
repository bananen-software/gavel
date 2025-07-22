package software.bananen.gavel.infrastructure.restapi;

import gavel.adapter.persistence.jpa.JpaAuthorEntity;
import gavel.adapter.persistence.jpa.JpaClassComplexityEntity;
import gavel.adapter.persistence.jpa.JpaClassContributionEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import software.bananen.gavel.backend.services.usecases.AuthorComplexityMetricsResponseModel;
import gavel.adapter.persistence.jpa.JpaAuthorRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;

@RequestMapping(value = "/author-complexity-metrics")
@Controller
public class AuthorComplexityMetricsController {

    private final JpaAuthorRepository repository;

    public AuthorComplexityMetricsController(
            @Autowired final JpaAuthorRepository repository) {
        this.repository = repository;
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping
    public ResponseEntity<Collection<AuthorComplexityMetricsResponseModel>> getAuthorComplexityMetrics() {
        final Collection<AuthorComplexityMetricsResponseModel> results =
                new ArrayList<>();

        for (final JpaAuthorEntity authorEntity : repository.findAll()) {
            final int complexityDelta =
                    authorEntity.getClassContributions()
                            .stream()
                            .sorted(Comparator.comparing(JpaClassContributionEntity::getTimestamp))
                            .mapToInt(c -> c.getClassComplexities().stream().findFirst().map(JpaClassComplexityEntity::getAddedComplexity).orElse(0))
                            .sum();

            final int numberOfChanges =
                    authorEntity.getClassContributions().size();

            results.add(new AuthorComplexityMetricsResponseModel(
                    authorEntity.getName(),
                    authorEntity.getEmail(),
                    complexityDelta,
                    numberOfChanges,
                    numberOfChanges == 0 ? 0 : complexityDelta / (double) numberOfChanges
            ));
        }

        return ResponseEntity.ok(results);
    }
}
