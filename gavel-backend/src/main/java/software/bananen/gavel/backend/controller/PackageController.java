package software.bananen.gavel.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import software.bananen.gavel.backend.services.usecases.*;

import java.util.Collection;

@Controller
@RequestMapping(value = "/packages")
public class PackageController {

    private final ListPackagesUseCase listPackagesUseCase;
    private final FetchPackageUseCase fetchPackageUseCase;
    private final ListClassesInPackageUseCase listClassesInPackageUseCase;

    public PackageController(
            @Autowired ListPackagesUseCase listPackagesUseCase,
            @Autowired FetchPackageUseCase fetchPackageUseCase,
            @Autowired ListClassesInPackageUseCase listClassesInPackageUseCase) {
        this.listPackagesUseCase = listPackagesUseCase;
        this.fetchPackageUseCase = fetchPackageUseCase;
        this.listClassesInPackageUseCase = listClassesInPackageUseCase;
    }


    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/")
    public ResponseEntity<Collection<PackageOverviewResponseModel>> loadPackages() {
        return listPackagesUseCase.load()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/{packageName}")
    public ResponseEntity<PackageOverviewResponseModel> loadPackages(
            @PathVariable String packageName) {
        return fetchPackageUseCase.load(packageName)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());

    }


    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/{packageName}/classes")
    public ResponseEntity<Collection<ClassOverviewResponseModel>> loadClassesInPackage(
            @PathVariable String packageName) {
        return listClassesInPackageUseCase.list(packageName)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());

    }
}
