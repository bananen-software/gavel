package software.bananen.gavel.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import software.bananen.gavel.backend.entity.AuthorEntity;
import software.bananen.gavel.backend.entity.ClassComplexityEntity;
import software.bananen.gavel.backend.entity.ClassContributionEntity;
import software.bananen.gavel.backend.repository.AuthorRepository;
import software.bananen.gavel.backend.services.usecases.AuthorComplexityMetricsResponseModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;

@RequestMapping(value = "/author-complexity-metrics")
@Controller
public class AuthorComplexityMetricsController {

    private final AuthorRepository repository;

    public AuthorComplexityMetricsController(
            @Autowired final AuthorRepository repository) {
        this.repository = repository;
    }
    
    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping
    public ResponseEntity<Collection<AuthorComplexityMetricsResponseModel>>
    getAuthorComplexityMetrics() {
        final Collection<AuthorComplexityMetricsResponseModel> results =
                new ArrayList<>();

        for (final AuthorEntity authorEntity : repository.findAll()) {
            final int complexityDelta =
                    authorEntity.getClassContributions()
                            .stream()
                            .sorted(Comparator.comparing(ClassContributionEntity::getTimestamp))
                            .mapToInt(c -> c.getClassComplexities().stream().findFirst().map(ClassComplexityEntity::getAddedComplexity).orElse(0))
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
