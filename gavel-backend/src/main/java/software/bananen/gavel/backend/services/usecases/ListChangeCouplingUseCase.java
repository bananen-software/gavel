package software.bananen.gavel.backend.services.usecases;

import org.springframework.stereotype.Service;
import software.bananen.gavel.backend.entity.ChangeCouplingEntity;
import software.bananen.gavel.backend.repository.ChangeCouplingRepository;

import java.util.Collection;
import java.util.function.Function;

@Service
public class ListChangeCouplingUseCase {

    private final ChangeCouplingRepository couplingRepository;

    public ListChangeCouplingUseCase(final ChangeCouplingRepository couplingRepository) {
        this.couplingRepository = couplingRepository;
    }

    public Collection<ChangeCouplingResponseModel> load() {
        return couplingRepository.findAll()
                .stream()
                .filter(c -> c.getCoupledChanges() > 10 && c.getChangeCoupling() > 0.25)
                .map(toResponseModel())
                .toList();
    }

    private static Function<ChangeCouplingEntity, ChangeCouplingResponseModel> toResponseModel() {
        return c -> new ChangeCouplingResponseModel(
                c.getSourceClass().getPackageField().getPackageName(),
                c.getSourceClass().getName(),
                c.getTargetClass().getPackageField().getPackageName(),
                c.getTargetClass().getName(),
                c.getTotalChanges(),
                c.getCoupledChanges(),
                c.getChangeCoupling()
        );
    }
}
