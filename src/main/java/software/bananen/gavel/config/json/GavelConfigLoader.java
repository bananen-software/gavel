package software.bananen.gavel.config.json;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

/**
 * A loader for the {@link GavelConfig}
 */
public class GavelConfigLoader {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private final File configFile;

    /**
     * Creates a new instance.
     *
     * @param configFile The config file.
     */
    public GavelConfigLoader(final File configFile) {

        this.configFile = configFile;
    }

    /**
     * Loads the configuration.
     *
     * @return The loaded configuration.
     * @throws GavelConfigLoaderException Might be thrown in case that loading the
     *                                    config failed
     */
    public GavelConfig loadConfig() throws GavelConfigLoaderException {
        try {
            return MAPPER.readValue(configFile, GavelConfig.class);
        } catch (IOException e) {
            throw new GavelConfigLoaderException("Failed to load the config.json file", e);
        }
    }
}
