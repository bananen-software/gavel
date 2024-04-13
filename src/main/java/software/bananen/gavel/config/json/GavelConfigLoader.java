package software.bananen.gavel.config.json;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * A loader for the {@link GavelConfig}
 */
public class GavelConfigLoader {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * Loads the configuration.
     *
     * @return The loaded configuration.
     * @throws GavelConfigLoaderException Might be thrown in case that loading the
     *                                    config failed
     */
    public GavelConfig loadConfig() throws GavelConfigLoaderException {
        try {
            //TODO: The path should be configurable
            return MAPPER.readValue(getClass().getClassLoader().getResourceAsStream("config.json"),
                    GavelConfig.class);
        } catch (IOException e) {
            throw new GavelConfigLoaderException("Failed to load the config.json file", e);
        }
    }
}
