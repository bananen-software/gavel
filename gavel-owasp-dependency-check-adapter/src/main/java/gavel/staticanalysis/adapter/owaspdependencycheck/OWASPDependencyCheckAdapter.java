package gavel.staticanalysis.adapter.owaspdependencycheck;

import io.github.jeremylong.openvulnerability.client.nvd.CvssV2;
import io.github.jeremylong.openvulnerability.client.nvd.CvssV2Data;
import io.github.jeremylong.openvulnerability.client.nvd.CvssV3;
import io.github.jeremylong.openvulnerability.client.nvd.CvssV3Data;
import io.github.jeremylong.openvulnerability.client.nvd.CvssV4;
import io.github.jeremylong.openvulnerability.client.nvd.CvssV4Data;
import org.owasp.dependencycheck.Engine;
import org.owasp.dependencycheck.data.update.exception.UpdateException;
import org.owasp.dependencycheck.dependency.Dependency;
import org.owasp.dependencycheck.dependency.Vulnerability;
import org.owasp.dependencycheck.exception.ExceptionCollection;
import org.owasp.dependencycheck.utils.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.bananen.gavel.domain.ports.service.StaticAnalysisAdapterException;
import software.bananen.gavel.domain.ports.service.VulnerabilityCheckAdapter;
import software.bananen.gavel.domain.ports.service.VulnerabilityFinding;
import software.bananen.gavel.domain.ports.service.VulnerableDependency;
import software.bananen.gavel.domain.service.RateCVEScoreService;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;

/**
 * An adapter that allows to integrate the OWASP dependency check with gavel.
 */
public final class OWASPDependencyCheckAdapter implements VulnerabilityCheckAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(OWASPDependencyCheckAdapter.class);

    private final Settings settings;

    /**
     * Creates a new instance.
     *
     * @param dataDirectory The path to the directory that data should be stored in.
     * @param nvdApiKey     The API key for the NVD API.
     * @param enableYarn    A flag that enables the yarn integration.
     * @param enablePnpm    A flag that enables the pnpm integration.
     */
    public OWASPDependencyCheckAdapter(final String dataDirectory,
                                       final String nvdApiKey,
                                       final boolean enableYarn,
                                       final boolean enablePnpm) {
        settings = new Settings();

        settings.setString(Settings.KEYS.DATA_DIRECTORY, dataDirectory);
        settings.setStringIfNotEmpty(Settings.KEYS.DB_DRIVER_NAME, "org.h2.Driver");
        settings.setBooleanIfNotNull(Settings.KEYS.AUTO_UPDATE, true);

        //TODO: Make this configurable?
        settings.setBooleanIfNotNull(Settings.KEYS.ANALYZER_YARN_AUDIT_ENABLED, enableYarn);
        settings.setBooleanIfNotNull(Settings.KEYS.ANALYZER_PNPM_AUDIT_ENABLED, enablePnpm);

        settings.setBooleanIfNotNull(Settings.KEYS.UPDATE_NVDCVE_ENABLED, true);
        settings.setStringIfNotEmpty(Settings.KEYS.NVD_API_KEY, nvdApiKey);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateSources() throws StaticAnalysisAdapterException {
        try (Engine engine = new Engine(settings)) {
            LOGGER.info("Perform updates");
            try {
                engine.doUpdates();
            } catch (final UpdateException e) {
                throw new StaticAnalysisAdapterException("Failed to update dependencies", e);
            }
            LOGGER.info("Completed updates");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<VulnerableDependency> checkDependencies(final File projectPath) throws StaticAnalysisAdapterException {
        try (Engine engine = new Engine(settings)) {
            LOGGER.info("Scanning project path: {}", projectPath);
            engine.scan(projectPath);
            LOGGER.info("Scanned project path: {}", projectPath);

            LOGGER.info("Analyzing dependencies of project: {}", projectPath);
            try {
                engine.analyzeDependencies();
            } catch (ExceptionCollection e) {
                throw new StaticAnalysisAdapterException("Failed to analyze dependencies", e);
            }
            LOGGER.info("Analyzed dependencies of project: {}", projectPath);

            return Arrays.stream(engine.getDependencies()).map(mapDependency()).toList();
        }
    }

    /**
     * Maps the owasp internal dependency representation to the appropriate models.
     *
     * @return The mapping function.
     */
    private static Function<Dependency, VulnerableDependency> mapDependency() {
        return dependency -> new VulnerableDependency(
                dependency.getName(),
                dependency.getFileName(),
                dependency.getFilePath(),
                dependency.getVulnerabilitiesCount(),
                dependency.getVulnerabilities().stream().map(mapVulnerability()).toList()
        );
    }

    /**
     * Maps the owasp internal vulnerability representation to the appropriate models.
     *
     * @return The mapping function.
     */
    private static Function<? super Vulnerability, VulnerabilityFinding> mapVulnerability() {
        return vulnerability -> new VulnerabilityFinding(
                vulnerability.getName(),
                vulnerability.getDescription(),
                getScore(vulnerability),
                new RateCVEScoreService().rate(getScore(vulnerability)),
                "https://nvd.nist.gov/vuln/detail/" + vulnerability.getName()
        );
    }

    /**
     * Retrieves the score from the given vulnerability.
     *
     * @param vulnerability The vulnerability.
     * @return The score.
     */
    private static Double getScore(final Vulnerability vulnerability) {
        return Optional.ofNullable(vulnerability.getCvssV4())
                .map(CvssV4::getCvssData)
                .map(CvssV4Data::getBaseScore)
                .orElse(Optional.ofNullable(vulnerability.getCvssV3())
                        .map(CvssV3::getCvssData)
                        .map(CvssV3Data::getBaseScore)
                        .orElse(Optional.ofNullable(vulnerability.getCvssV2())
                                .map(CvssV2::getCvssData)
                                .map(CvssV2Data::getBaseScore)
                                .orElse(0.0)));
    }
}
