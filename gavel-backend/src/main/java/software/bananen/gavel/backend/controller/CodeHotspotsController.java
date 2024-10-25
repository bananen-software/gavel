package software.bananen.gavel.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import software.bananen.gavel.backend.services.usecases.CodeHotspotResponseModel;
import software.bananen.gavel.backend.services.usecases.ListCodeHotspotsUseCase;

import java.util.Collection;

@RequestMapping(value = "/code-hotspots")
@Controller
public class CodeHotspotsController {

    private final ListCodeHotspotsUseCase useCase;

    public CodeHotspotsController(@Autowired ListCodeHotspotsUseCase useCase) {
        this.useCase = useCase;
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping
    public ResponseEntity<Collection<CodeHotspotResponseModel>> list() {
        return useCase.load()
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
