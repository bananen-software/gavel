package software.bananen.gavel.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import software.bananen.gavel.backend.services.usecases.LoadRelationalCohesionMetricsResponseModel;
import software.bananen.gavel.backend.services.usecases.LoadRelationalCohesionMetricsUseCase;

import java.util.Collection;

@RequestMapping(value = "/relational-cohesion-metrics")
@Controller
public class RelationalCohesionMetricsController {

    private final LoadRelationalCohesionMetricsUseCase useCase;

    public RelationalCohesionMetricsController(
            @Autowired final LoadRelationalCohesionMetricsUseCase useCase) {
        this.useCase = useCase;
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping
    public ResponseEntity<Collection<LoadRelationalCohesionMetricsResponseModel>> fetch() {
        return useCase.load()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
