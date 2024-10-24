package software.bananen.gavel.backend;

import gavel.staticanalysis.adapter.owaspdependencycheck.OWASPDependencyCheckAdapter;
import gavel.staticanalysis.adapter.pmd.PMDAdapter;
import gavel.staticanalysis.adapter.spotbugs.SpotbugsAdapter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;

@Configuration
public class GavelConfiguration {

    @Value("${gavel.spotbugs.java-home}")
    Path javaHome;
    @Value("${gavel.owasp.dependencycheck.data-directory}")
    String dataDirectory;
    @Value("${gavel.owasp.dependencycheck.nvd-api-key}")
    String nvdApiKey;
    @Value("${gavel.owasp.dependencycheck.yarn.enabled}")
    boolean enableYarn;
    @Value("${gavel.owasp.dependencycheck.pnpm.enabled}")
    boolean enablePnpm;

    @Bean
    public PMDAdapter pmdAdapter() {
        return new PMDAdapter();
    }

    @Bean
    public SpotbugsAdapter spotbugsAdapter() {
        return new SpotbugsAdapter(javaHome);
    }

    @Bean
    public OWASPDependencyCheckAdapter owaspDependencyCheckAdapter() {
        return new OWASPDependencyCheckAdapter(dataDirectory, nvdApiKey, enableYarn, enablePnpm);
    }
}
