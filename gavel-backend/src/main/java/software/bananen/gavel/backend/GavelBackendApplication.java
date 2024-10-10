package software.bananen.gavel.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(
        exclude = {
                SecurityAutoConfiguration.class,
                ManagementWebSecurityAutoConfiguration.class
        },
        scanBasePackages = {"software.bananen.gavel"}
)
public class GavelBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(GavelBackendApplication.class, args);
    }
}
