package software.bananen.gavel.backend;

import org.springframework.boot.SpringApplication;

public class TestGavelBackendApplication {

	public static void main(String[] args) {
		SpringApplication.from(GavelBackendApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
