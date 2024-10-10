package software.bananen.gavel.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import software.bananen.gavel.backend.services.usecases.LoadCumulativeComponentDependencyMetricsResponseModel;
import software.bananen.gavel.backend.services.usecases.LoadCumulativeComponentDependencyMetricsUseCase;

import java.util.Collection;

@RequestMapping(value = "/cumulative-component-dependency-metrics")
@Controller
public class CumulativeComponentDependencyMetricsController {

    private final LoadCumulativeComponentDependencyMetricsUseCase useCase;

    public CumulativeComponentDependencyMetricsController(
            @Autowired final LoadCumulativeComponentDependencyMetricsUseCase useCase) {
        this.useCase = useCase;
    }


    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping
    public ResponseEntity<Collection<LoadCumulativeComponentDependencyMetricsResponseModel>> fetch() {
        return useCase.load().map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
