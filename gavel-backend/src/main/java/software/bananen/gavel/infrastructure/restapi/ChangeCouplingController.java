package software.bananen.gavel.infrastructure.restapi;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import software.bananen.gavel.backend.services.usecases.ChangeCouplingResponseModel;
import software.bananen.gavel.backend.services.usecases.ListChangeCouplingUseCase;

import java.util.Collection;

@RequestMapping(value = "/change-coupling")
@Controller
public class ChangeCouplingController {

    private final ListChangeCouplingUseCase useCase;

    public ChangeCouplingController(@Autowired final ListChangeCouplingUseCase useCase) {
        this.useCase = useCase;
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping
    public ResponseEntity<Collection<ChangeCouplingResponseModel>> list() {
        return ResponseEntity.ok(useCase.load());
    }
}
