package software.bananen.gavel.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import software.bananen.gavel.backend.services.usecases.LoadVisibilityMetricsResponseModel;
import software.bananen.gavel.backend.services.usecases.LoadVisibilityMetricsUseCase;

import java.util.Collection;

@RequestMapping(value = "/component-visibility-metrics")
@Controller
public class ComponentVisibilityMetricsController {

    private final LoadVisibilityMetricsUseCase useCase;

    public ComponentVisibilityMetricsController(
            @Autowired LoadVisibilityMetricsUseCase useCase) {
        this.useCase = useCase;
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping
    public ResponseEntity<Collection<LoadVisibilityMetricsResponseModel>> fetch() {
        return useCase.load()
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
