package software.bananen.gavel.config.json;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class ConfigLoader {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * Loads the configuration.
     *
     * @return The loaded configuration.
     * @throws ConfigLoaderException Might be thrown in case that loading the
     *                               config failed
     */
    public GavelConfig loadConfig() throws ConfigLoaderException {
        try {
            return MAPPER.readValue(getClass().getClassLoader().getResourceAsStream("config.json"),
                    GavelConfig.class);
        } catch (IOException e) {
            throw new ConfigLoaderException("Failed to load the config.json file", e);
        }
    }
}
