package software.bananen.gavel.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import software.bananen.gavel.backend.services.usecases.LoadComponentDependencyMetricsResponseModel;
import software.bananen.gavel.backend.services.usecases.LoadComponentDependencyMetricsUseCase;

import java.util.Collection;

@RequestMapping(value = "/component-dependency-metrics")
@Controller
public class ComponentDependencyMetricsController {

    private final LoadComponentDependencyMetricsUseCase loadUseCase;

    public ComponentDependencyMetricsController(@Autowired LoadComponentDependencyMetricsUseCase useCase) {
        this.loadUseCase = useCase;
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping
    public ResponseEntity<Collection<LoadComponentDependencyMetricsResponseModel>> fetchMetrics() {
        return loadUseCase.load()
                .map(res -> ResponseEntity.ok().body(res))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
